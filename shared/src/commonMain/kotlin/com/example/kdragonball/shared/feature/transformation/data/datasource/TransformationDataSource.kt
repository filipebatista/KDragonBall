package com.example.kdragonball.shared.feature.transformation.data.datasource

import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationDetail

/**
 * Data source interface for fetching transformation data from the API
 */
interface TransformationDataSource {
    /**
     * Fetch list of transformations from the API
     * Note: The transformations API returns a direct array, not a paginated response
     */
    suspend fun fetchTransformations(): List<TransformationDetail>

    /**
     * Fetch a specific transformation by ID
     */
    suspend fun fetchTransformation(transformationId: Int): TransformationDetail

    /**
     * Search transformations by name
     */
    suspend fun searchTransformations(query: String): List<TransformationDetail>
}
