package com.example.kdragonball.shared.feature.planet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kdragonball.shared.feature.planet.domain.usecase.GetPlanets
import com.example.kdragonball.shared.feature.planet.domain.usecase.SearchPlanets
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the planet list screen
 */
class PlanetListViewModel(
    private val getPlanets: GetPlanets,
    private val searchPlanetsUseCase: SearchPlanets
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlanetListUiState())
    val uiState: StateFlow<PlanetListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PlanetEvent>()
    val events = _events.asSharedFlow()

    init {
        loadPlanets()
    }

    fun loadPlanets(page: Int = 1) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getPlanets(page = page, limit = 20).fold(
                ifLeft = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    _events.emit(PlanetEvent.ShowError(error.message))
                },
                ifRight = { data ->
                    _uiState.update {
                        it.copy(
                            planets = if (page == 1) data else it.planets + data,
                            isLoading = false,
                            currentPage = page,
                            hasMorePages = data.isNotEmpty()
                        )
                    }
                }
            )
        }
    }

    fun loadNextPage() {
        if (!_uiState.value.isLoading && _uiState.value.hasMorePages) {
            loadPlanets(_uiState.value.currentPage + 1)
        }
    }

    fun searchPlanets(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            // Reload the full list when search is cleared
            loadPlanets(page = 1)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            searchPlanetsUseCase(query).fold(
                ifLeft = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    _events.emit(PlanetEvent.ShowError(error.message))
                },
                ifRight = { data ->
                    _uiState.update {
                        it.copy(
                            planets = data,
                            isLoading = false,
                            hasMorePages = false
                        )
                    }
                }
            )
        }
    }

    fun onPlanetClick(planetId: Int) {
        viewModelScope.launch {
            _events.emit(PlanetEvent.NavigateToDetail(planetId))
        }
    }

    fun refresh() {
        loadPlanets(page = 1)
    }
}
