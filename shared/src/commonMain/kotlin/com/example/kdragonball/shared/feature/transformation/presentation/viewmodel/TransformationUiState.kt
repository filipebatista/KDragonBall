package com.example.kdragonball.shared.feature.transformation.presentation.viewmodel

import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationDetail

/**
 * UI state for the transformation list screen
 */
data class TransformationListUiState(
    val transformations: List<TransformationDetail> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true
) {
    val isEmpty: Boolean
        get() = transformations.isEmpty() && !isLoading

    val filteredTransformations: List<TransformationDetail>
        get() =
            if (searchQuery.isBlank()) {
                transformations
            } else {
                transformations.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }
}

/**
 * UI state for the transformation detail screen
 */
data class TransformationDetailUiState(
    val transformation: TransformationDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Events for transformation screens
 */
sealed class TransformationEvent {
    data class ShowError(val message: String) : TransformationEvent()

    data class NavigateToDetail(val transformationId: Int) : TransformationEvent()

    data object NavigateBack : TransformationEvent()
}
