package com.example.kdragonball.shared.feature.character.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a Dragon Ball character with all their attributes
 */
@Serializable
data class Character(
    val id: Int,
    val name: String,
    val ki: String,
    val maxKi: String,
    val race: String,
    val gender: String,
    val description: String,
    val image: String,
    val affiliation: String,
    val originPlanet: OriginPlanet? = null,
    val transformations: List<Transformation> = emptyList()
) {
    val isHero: Boolean
        get() = affiliation.contains("Army", ignoreCase = true).not()
}

@Serializable
data class OriginPlanet(
    val id: Int? = null,
    val name: String,
    val isDestroyed: Boolean = false,
    val description: String = ""
)

@Serializable
data class Transformation(
    val id: Int,
    val name: String,
    val image: String,
    val ki: String
)
