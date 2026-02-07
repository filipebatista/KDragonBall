package com.example.kdragonball.shared.feature.transformation.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a Dragon Ball transformation with full details
 * Used for standalone transformation list/detail screens
 */
@Serializable
data class TransformationDetail(
    val id: Int,
    val name: String,
    val image: String,
    val ki: String,
    val deletedAt: String? = null
)
