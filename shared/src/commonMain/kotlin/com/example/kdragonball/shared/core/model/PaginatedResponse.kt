package com.example.kdragonball.shared.core.model

import kotlinx.serialization.Serializable

/**
 * Wrapper for paginated API responses
 */
@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val meta: Meta,
    val links: Links
)

@Serializable
data class Meta(
    val totalItems: Int,
    val itemCount: Int,
    val itemsPerPage: Int,
    val totalPages: Int,
    val currentPage: Int
)

@Serializable
data class Links(
    val first: String,
    val previous: String? = null,
    val next: String? = null,
    val last: String
)
