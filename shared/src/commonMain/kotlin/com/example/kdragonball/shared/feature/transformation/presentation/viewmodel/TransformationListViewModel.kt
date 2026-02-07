package com.example.kdragonball.shared.feature.transformation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kdragonball.shared.feature.transformation.domain.usecase.GetTransformations
import com.example.kdragonball.shared.feature.transformation.domain.usecase.SearchTransformations
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the transformation list screen
 */
class TransformationListViewModel(
    private val getTransformations: GetTransformations,
    private val searchTransformationsUseCase: SearchTransformations
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransformationListUiState())
    val uiState: StateFlow<TransformationListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TransformationEvent>()
    val events = _events.asSharedFlow()

    init {
        loadTransformations()
    }

    fun loadTransformations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getTransformations().fold(
                ifLeft = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    _events.emit(TransformationEvent.ShowError(error.message))
                },
                ifRight = { data ->
                    _uiState.update {
                        it.copy(
                            transformations = data,
                            isLoading = false,
                            hasMorePages = false
                        )
                    }
                }
            )
        }
    }

    fun searchTransformations(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            // Reload the full list when search is cleared
            loadTransformations()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            searchTransformationsUseCase(query).fold(
                ifLeft = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    _events.emit(TransformationEvent.ShowError(error.message))
                },
                ifRight = { data ->
                    _uiState.update {
                        it.copy(
                            transformations = data,
                            isLoading = false,
                            hasMorePages = false
                        )
                    }
                }
            )
        }
    }

    fun onTransformationClick(transformationId: Int) {
        viewModelScope.launch {
            _events.emit(TransformationEvent.NavigateToDetail(transformationId))
        }
    }

    fun refresh() {
        loadTransformations()
    }
}
