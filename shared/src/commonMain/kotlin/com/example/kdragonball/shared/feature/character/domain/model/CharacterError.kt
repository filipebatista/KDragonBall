package com.example.kdragonball.shared.feature.character.domain.model

sealed class CharacterError(open val message: String) {
    data class NotFound(
        override val message: String = "Character not found"
    ) : CharacterError(message)

    data class NetworkError(override val message: String = "Network error") : CharacterError(
        message
    )

    data class UnknownError(override val message: String = "Unknown error") : CharacterError(
        message
    )
}
