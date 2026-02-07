package com.example.kdragonball.shared.feature.transformation.domain.repository

import arrow.core.Either
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationDetail
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationError
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing Dragon Ball transformation data
 */
interface TransformationRepository {
    /**
     * Observe all transformations with real-time updates
     */
    fun observeTransformations(): Flow<List<TransformationDetail>>

    /**
     * Observe a specific transformation by ID with real-time updates
     */
    fun observeTransformation(transformationId: Int): Flow<TransformationDetail?>

    /**
     * Get list of all transformations
     * Note: The transformations API returns all items in a single response
     */
    suspend fun getTransformations(): Either<TransformationError, List<TransformationDetail>>

    /**
     * Get a specific transformation by ID
     */
    suspend fun getTransformation(
        transformationId: Int
    ): Either<TransformationError, TransformationDetail>

    /**
     * Search transformations by name
     */
    suspend fun searchTransformations(
        query: String
    ): Either<TransformationError, List<TransformationDetail>>
}
