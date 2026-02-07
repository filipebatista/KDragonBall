package com.example.kdragonball.shared.feature.transformation.domain.usecase

import arrow.core.right
import com.example.kdragonball.shared.core.testutil.TestBuilders
import com.example.kdragonball.shared.feature.transformation.domain.FakeTransformationRepository
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationError
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class GetTransformationDetailsTest {
    private lateinit var repository: FakeTransformationRepository
    private lateinit var getTransformationDetails: GetTransformationDetails

    @BeforeTest
    fun setUp() {
        repository = FakeTransformationRepository()
        getTransformationDetails = GetTransformationDetails(repository)
    }

    @Test
    fun `given transformation exists when getting transformation details then returns success with transformation`() =
        runTest {
            // Given
            val transformation = TestBuilders.transformationDetail(id = 1, name = "Super Saiyan")
            repository.setTransformations(listOf(transformation))

            // When
            val result = getTransformationDetails(transformationId = 1)

            // Then
            assertTrue(result.isRight())
            result.onRight { data ->
                assertEquals("Super Saiyan", data.name)
            }
        }

    @Test
    fun `given transformation does not exist when getting transformation details then returns not found error`() =
        runTest {
            // Given
            repository.setTransformations(emptyList())

            // When
            val result = getTransformationDetails(transformationId = 999)

            // Then
            assertTrue(result.isLeft())
            result.onLeft { error ->
                assertTrue(error is TransformationError.NotFound)
            }
        }

    @Test
    fun `given repository fails when getting transformation details then returns error`() =
        runTest {
            // Given
            repository.shouldFailWithError = TransformationError.NetworkError("Connection failed")

            // When
            val result = getTransformationDetails(transformationId = 1)

            // Then
            assertTrue(result.isLeft())
            result.onLeft { error ->
                assertTrue(error is TransformationError.NetworkError)
            }
        }

    @Test
    fun `given transformation id when getting transformation details then passes correct id to repository`() =
        runTest {
            // Given
            val transformation = TestBuilders.transformationDetail(id = 42)
            repository.setTransformations(listOf(transformation))

            // When
            getTransformationDetails(transformationId = 42)

            // Then
            assertEquals(1, repository.getTransformationCalls.size)
            assertEquals(42, repository.getTransformationCalls.first())
        }

    @Test
    fun `given transformation with ki when getting details then returns transformation with ki value`() =
        runTest {
            // Given
            val transformation = TestBuilders.transformationDetail(
                id = 1,
                name = "Super Saiyan Blue",
                ki = "5 Quintillion"
            )
            repository.setTransformations(listOf(transformation))

            // When
            val result = getTransformationDetails(transformationId = 1)

            // Then
            assertTrue(result.isRight())
            result.onRight { returnedTransformation ->
                assertEquals("Super Saiyan Blue", returnedTransformation.name)
                assertEquals("5 Quintillion", returnedTransformation.ki)
            }
        }

    @Test
    fun `given repository configured to return specific transformation when getting details then returns that transformation`() =
        runTest {
            // Given
            val customTransformation = TestBuilders.transformationDetail(
                id = 99,
                name = "Ultra Instinct"
            )
            repository.getTransformationResult = customTransformation.right()

            // When
            val result = getTransformationDetails(transformationId = 1)

            // Then
            assertTrue(result.isRight())
            result.onRight { data ->
                assertEquals("Ultra Instinct", data.name)
            }
        }
}
