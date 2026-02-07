package com.example.kdragonball.shared.feature.transformation.domain.usecase

import arrow.core.Either
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationDetail
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationError
import com.example.kdragonball.shared.feature.transformation.domain.repository.TransformationRepository

/**
 * Use case for retrieving details of a specific Dragon Ball transformation
 */
class GetTransformationDetails(
    private val repository: TransformationRepository
) {
    suspend operator fun invoke(
        transformationId: Int
    ): Either<TransformationError, TransformationDetail> {
        return repository.getTransformation(transformationId)
    }
}
