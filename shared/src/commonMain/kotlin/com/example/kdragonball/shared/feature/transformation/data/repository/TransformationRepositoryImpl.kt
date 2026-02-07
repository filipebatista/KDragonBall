package com.example.kdragonball.shared.feature.transformation.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.kdragonball.shared.feature.transformation.data.datasource.TransformationDataSource
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationDetail
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationError
import com.example.kdragonball.shared.feature.transformation.domain.repository.TransformationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Implementation of TransformationRepository using the API data source
 */
class TransformationRepositoryImpl(
    private val dataSource: TransformationDataSource
) : TransformationRepository {
    // Cache for transformations
    private val transformationsCache = MutableStateFlow<Map<Int, TransformationDetail>>(emptyMap())

    override fun observeTransformations(): Flow<List<TransformationDetail>> {
        return transformationsCache.map { it.values.toList() }
    }

    override fun observeTransformation(transformationId: Int): Flow<TransformationDetail?> {
        return transformationsCache.map { it[transformationId] }
    }

    override suspend fun getTransformations(): Either<TransformationError, List<TransformationDetail>> {
        return try {
            val transformations = dataSource.fetchTransformations()

            // Update cache
            transformationsCache.value = transformationsCache.value + transformations.associateBy { it.id }

            transformations.right()
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun getTransformation(
        transformationId: Int
    ): Either<TransformationError, TransformationDetail> {
        return try {
            // Check cache first
            transformationsCache.value[transformationId]?.let {
                return it.right()
            }

            // Fetch from API
            val transformation = dataSource.fetchTransformation(transformationId)

            // Update cache
            transformationsCache.value = transformationsCache.value + (transformation.id to transformation)

            transformation.right()
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun searchTransformations(
        query: String
    ): Either<TransformationError, List<TransformationDetail>> {
        return try {
            if (query.isBlank()) {
                return transformationsCache.value.values.toList().right()
            }

            val transformations = dataSource.searchTransformations(query)

            // Update cache
            transformationsCache.value = transformationsCache.value + transformations.associateBy { it.id }

            transformations.right()
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun <T> handleException(e: Exception): Either<TransformationError, T> {
        return when {
            e.message?.contains("404") == true -> {
                TransformationError.NotFound("Transformation not found").left()
            }
            e.message?.contains("network") == true || e.message?.contains("timeout") == true -> {
                TransformationError.NetworkError("Network error: ${e.message}").left()
            }
            else -> {
                TransformationError.UnknownError("Unknown error: ${e.message}").left()
            }
        }
    }
}
