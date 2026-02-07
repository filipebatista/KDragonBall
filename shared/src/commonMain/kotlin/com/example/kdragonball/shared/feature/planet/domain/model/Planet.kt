package com.example.kdragonball.shared.feature.planet.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a Dragon Ball planet with all attributes
 */
@Serializable
data class Planet(
    val id: Int,
    val name: String,
    val isDestroyed: Boolean = false,
    val description: String = "",
    val image: String = "",
    val deletedAt: String? = null
)
