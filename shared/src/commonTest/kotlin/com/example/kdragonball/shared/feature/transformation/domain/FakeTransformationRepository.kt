package com.example.kdragonball.shared.feature.transformation.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationDetail
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationError
import com.example.kdragonball.shared.feature.transformation.domain.repository.TransformationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of TransformationRepository for testing.
 * Allows configuring responses and tracking method calls.
 */
class FakeTransformationRepository : TransformationRepository {
    private val transformationsStore = MutableStateFlow<Map<Int, TransformationDetail>>(emptyMap())

    // Configuration for controlling behavior
    var getTransformationsResult: Either<TransformationError, List<TransformationDetail>>? = null
    var getTransformationResult: Either<TransformationError, TransformationDetail>? = null
    var searchTransformationsResult: Either<TransformationError, List<TransformationDetail>>? = null

    // Error simulation
    var shouldFailWithError: TransformationError? = null

    // Call tracking
    var getTransformationsCalls = 0
    var getTransformationCalls = mutableListOf<Int>() // transformationId
    var searchTransformationsCalls = mutableListOf<String>() // query

    /**
     * Prepopulate the store with transformations for testing
     */
    fun setTransformations(transformations: List<TransformationDetail>) {
        transformationsStore.value = transformations.associateBy { it.id }
    }

    fun clearTransformations() {
        transformationsStore.value = emptyMap()
    }

    fun reset() {
        transformationsStore.value = emptyMap()
        getTransformationsResult = null
        getTransformationResult = null
        searchTransformationsResult = null
        shouldFailWithError = null
        getTransformationsCalls = 0
        getTransformationCalls.clear()
        searchTransformationsCalls.clear()
    }

    override fun observeTransformations(): Flow<List<TransformationDetail>> {
        return transformationsStore.map { it.values.toList() }
    }

    override fun observeTransformation(transformationId: Int): Flow<TransformationDetail?> {
        return transformationsStore.map { it[transformationId] }
    }

    override suspend fun getTransformations(): Either<TransformationError, List<TransformationDetail>> {
        getTransformationsCalls++

        shouldFailWithError?.let { error ->
            return error.left()
        }

        getTransformationsResult?.let { return it }

        // Default behavior: return all transformations from store
        return transformationsStore.value.values.toList().right()
    }

    override suspend fun getTransformation(
        transformationId: Int
    ): Either<TransformationError, TransformationDetail> {
        getTransformationCalls.add(transformationId)

        shouldFailWithError?.let { error ->
            return error.left()
        }

        getTransformationResult?.let { return it }

        // Default behavior: return transformation from store
        val transformation = transformationsStore.value[transformationId]
        return if (transformation != null) {
            transformation.right()
        } else {
            TransformationError.NotFound(
                "Transformation with id $transformationId not found"
            ).left()
        }
    }

    override suspend fun searchTransformations(
        query: String
    ): Either<TransformationError, List<TransformationDetail>> {
        searchTransformationsCalls.add(query)

        shouldFailWithError?.let { error ->
            return error.left()
        }

        searchTransformationsResult?.let { return it }

        // Default behavior: filter transformations by name
        val filteredTransformations =
            transformationsStore.value.values
                .filter { it.name.contains(query, ignoreCase = true) }
        return filteredTransformations.right()
    }
}
