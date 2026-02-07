package com.example.kdragonball.shared.feature.transformation.domain.usecase

import arrow.core.getOrElse
import com.example.kdragonball.shared.core.testutil.TestBuilders
import com.example.kdragonball.shared.feature.transformation.domain.FakeTransformationRepository
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationError
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class GetTransformationsTest {
    private lateinit var repository: FakeTransformationRepository
    private lateinit var getTransformations: GetTransformations

    @BeforeTest
    fun setUp() {
        repository = FakeTransformationRepository()
        getTransformations = GetTransformations(repository)
    }

    @Test
    fun `given transformations exist when getting transformations then returns success with transformation list`() =
        runTest {
            // Given
            val transformations = TestBuilders.transformationDetailList(count = 5)
            repository.setTransformations(transformations)

            // When
            val result = getTransformations()

            // Then
            assertTrue(result.isRight())
            assertEquals(5, result.getOrElse { emptyList() }.size)
        }

    @Test
    fun `given no transformations exist when getting transformations then returns empty list`() =
        runTest {
            // Given
            repository.setTransformations(emptyList())

            // When
            val result = getTransformations()

            // Then
            assertTrue(result.isRight())
            assertEquals(0, result.getOrElse { emptyList() }.size)
        }

    @Test
    fun `given repository fails when getting transformations then returns error`() = runTest {
        // Given
        repository.shouldFailWithError = TransformationError.NetworkError("Connection failed")

        // When
        val result = getTransformations()

        // Then
        assertTrue(result.isLeft())
        result.onLeft { error ->
            assertTrue(error is TransformationError.NetworkError)
        }
    }

    @Test
    fun `given transformations exist when invoking use case then returns all transformations`() =
        runTest {
            // Given
            val transformations = TestBuilders.transformationDetailList(count = 15)
            repository.setTransformations(transformations)

            // When
            val result = getTransformations()

            // Then
            assertTrue(result.isRight())
            assertEquals(15, result.getOrElse { emptyList() }.size)
        }

    @Test
    fun `given use case invoked when getting transformations then repository is called once`() =
        runTest {
            // Given
            repository.setTransformations(emptyList())

            // When
            getTransformations()

            // Then
            assertEquals(1, repository.getTransformationsCalls)
        }
}
