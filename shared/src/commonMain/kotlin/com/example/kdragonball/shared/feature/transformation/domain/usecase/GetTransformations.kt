package com.example.kdragonball.shared.feature.transformation.domain.usecase

import arrow.core.Either
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationDetail
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationError
import com.example.kdragonball.shared.feature.transformation.domain.repository.TransformationRepository

/**
 * Use case for retrieving a list of Dragon Ball transformations
 */
class GetTransformations(
    private val repository: TransformationRepository
) {
    suspend operator fun invoke(): Either<TransformationError, List<TransformationDetail>> {
        return repository.getTransformations()
    }
}
