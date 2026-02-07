package com.example.kdragonball.shared.feature.planet.presentation.viewmodel

import com.example.kdragonball.shared.feature.planet.domain.model.Planet

/**
 * UI state for the planet list screen
 */
data class PlanetListUiState(
    val planets: List<Planet> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true
) {
    val isEmpty: Boolean
        get() = planets.isEmpty() && !isLoading

    val filteredPlanets: List<Planet>
        get() =
            if (searchQuery.isBlank()) {
                planets
            } else {
                planets.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }
}

/**
 * UI state for the planet detail screen
 */
data class PlanetDetailUiState(
    val planet: Planet? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Events for planet screens
 */
sealed class PlanetEvent {
    data class ShowError(val message: String) : PlanetEvent()

    data class NavigateToDetail(val planetId: Int) : PlanetEvent()

    data object NavigateBack : PlanetEvent()
}
