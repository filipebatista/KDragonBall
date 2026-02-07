package com.example.kdragonball.shared.feature.transformation.domain.usecase

import arrow.core.getOrElse
import arrow.core.right
import com.example.kdragonball.shared.core.testutil.TestBuilders
import com.example.kdragonball.shared.feature.transformation.domain.FakeTransformationRepository
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationError
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SearchTransformationsTest {
    private lateinit var repository: FakeTransformationRepository
    private lateinit var searchTransformations: SearchTransformations

    @BeforeTest
    fun setUp() {
        repository = FakeTransformationRepository()
        searchTransformations = SearchTransformations(repository)
    }

    @Test
    fun `given transformations exist when searching with matching query then returns matching transformations`() =
        runTest {
            // Given
            val transformations =
                listOf(
                    TestBuilders.transformationDetail(id = 1, name = "Super Saiyan"),
                    TestBuilders.transformationDetail(id = 2, name = "Super Saiyan 2"),
                    TestBuilders.transformationDetail(id = 3, name = "Ultra Instinct")
                )
            repository.setTransformations(transformations)

            // When
            val result = searchTransformations(query = "Super")

            // Then
            assertTrue(result.isRight())
            val foundTransformations = result.getOrElse { emptyList() }
            assertEquals(2, foundTransformations.size)
            assertTrue(foundTransformations.any { it.name == "Super Saiyan" })
            assertTrue(foundTransformations.any { it.name == "Super Saiyan 2" })
        }

    @Test
    fun `given transformations exist when searching with no matching query then returns empty list`() =
        runTest {
            // Given
            val transformations =
                listOf(
                    TestBuilders.transformationDetail(id = 1, name = "Super Saiyan"),
                    TestBuilders.transformationDetail(id = 2, name = "Ultra Instinct")
                )
            repository.setTransformations(transformations)

            // When
            val result = searchTransformations(query = "Kaioken")

            // Then
            assertTrue(result.isRight())
            assertEquals(0, result.getOrElse { emptyList() }.size)
        }

    @Test
    fun `given transformations exist when searching with case insensitive query then returns matching transformations`() =
        runTest {
            // Given
            val transformations =
                listOf(
                    TestBuilders.transformationDetail(id = 1, name = "SUPER SAIYAN"),
                    TestBuilders.transformationDetail(id = 2, name = "ultra instinct")
                )
            repository.setTransformations(transformations)

            // When
            val result = searchTransformations(query = "super saiyan")

            // Then
            assertTrue(result.isRight())
            val data = result.getOrElse { emptyList() }
            assertEquals(1, data.size)
            assertEquals("SUPER SAIYAN", data.first().name)
        }

    @Test
    fun `given repository configured to return specific result when searching then returns that result`() =
        runTest {
            // Given
            val expectedResults =
                listOf(TestBuilders.transformationDetail(id = 99, name = "Custom Transformation"))
            repository.searchTransformationsResult = expectedResults.right()

            // When
            val result = searchTransformations(query = "anything")

            // Then
            assertTrue(result.isRight())
            assertEquals("Custom Transformation", result.getOrElse { emptyList() }.first().name)
        }

    @Test
    fun `given repository fails when searching then returns error`() = runTest {
        // Given
        repository.shouldFailWithError = TransformationError.NetworkError("Search failed")

        // When
        val result = searchTransformations(query = "Super")

        // Then
        assertTrue(result.isLeft())
        result.onLeft { error ->
            assertTrue(error is TransformationError.NetworkError)
        }
    }

    @Test
    fun `given empty query when searching then passes empty query to repository`() = runTest {
        // Given
        repository.setTransformations(TestBuilders.transformationDetailList(count = 3))

        // When
        searchTransformations(query = "")

        // Then
        assertEquals(1, repository.searchTransformationsCalls.size)
        assertEquals("", repository.searchTransformationsCalls.first())
    }

    @Test
    fun `given search query when searching then passes exact query to repository`() = runTest {
        // Given
        repository.setTransformations(emptyList())

        // When
        searchTransformations(query = "Super Saiyan God")

        // Then
        assertEquals(1, repository.searchTransformationsCalls.size)
        assertEquals("Super Saiyan God", repository.searchTransformationsCalls.first())
    }
}
