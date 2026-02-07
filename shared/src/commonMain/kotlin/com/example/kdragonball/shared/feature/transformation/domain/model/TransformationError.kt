package com.example.kdragonball.shared.feature.transformation.domain.model

sealed class TransformationError(open val message: String) {
    data class NotFound(
        override val message: String = "Transformation not found"
    ) : TransformationError(message)

    data class NetworkError(override val message: String = "Network error") : TransformationError(
        message
    )

    data class UnknownError(override val message: String = "Unknown error") : TransformationError(
        message
    )
}
