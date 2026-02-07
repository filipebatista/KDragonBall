package com.example.kdragonball.shared.feature.planet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kdragonball.shared.feature.planet.domain.usecase.GetPlanetDetails
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the planet detail screen
 */
class PlanetDetailViewModel(
    private val getPlanetDetails: GetPlanetDetails,
    private val planetId: Int
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlanetDetailUiState())
    val uiState: StateFlow<PlanetDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PlanetEvent>()
    val events = _events.asSharedFlow()

    init {
        loadPlanetDetails()
    }

    private fun loadPlanetDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getPlanetDetails(planetId).fold(
                ifLeft = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    _events.emit(PlanetEvent.ShowError(error.message))
                },
                ifRight = { data ->
                    _uiState.update {
                        it.copy(
                            planet = data,
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _events.emit(PlanetEvent.NavigateBack)
        }
    }

    fun refresh() {
        loadPlanetDetails()
    }
}
