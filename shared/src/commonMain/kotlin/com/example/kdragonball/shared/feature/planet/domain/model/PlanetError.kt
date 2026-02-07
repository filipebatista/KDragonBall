package com.example.kdragonball.shared.feature.planet.domain.model

sealed class PlanetError(open val message: String) {
    data class NotFound(override val message: String = "Planet not found") : PlanetError(message)

    data class NetworkError(override val message: String = "Network error") : PlanetError(message)

    data class UnknownError(override val message: String = "Unknown error") : PlanetError(message)
}
