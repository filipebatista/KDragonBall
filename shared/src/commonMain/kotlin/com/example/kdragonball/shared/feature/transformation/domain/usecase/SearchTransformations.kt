package com.example.kdragonball.shared.feature.transformation.domain.usecase

import arrow.core.Either
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationDetail
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationError
import com.example.kdragonball.shared.feature.transformation.domain.repository.TransformationRepository

/**
 * Use case for searching Dragon Ball transformations by name
 */
class SearchTransformations(
    private val repository: TransformationRepository
) {
    suspend operator fun invoke(
        query: String
    ): Either<TransformationError, List<TransformationDetail>> {
        return repository.searchTransformations(query)
    }
}
