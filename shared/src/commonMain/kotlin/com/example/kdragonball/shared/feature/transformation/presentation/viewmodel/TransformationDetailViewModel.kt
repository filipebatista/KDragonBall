package com.example.kdragonball.shared.feature.transformation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kdragonball.shared.feature.transformation.domain.usecase.GetTransformationDetails
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the transformation detail screen
 */
class TransformationDetailViewModel(
    private val getTransformationDetails: GetTransformationDetails,
    private val transformationId: Int
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransformationDetailUiState())
    val uiState: StateFlow<TransformationDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TransformationEvent>()
    val events = _events.asSharedFlow()

    init {
        loadTransformationDetails()
    }

    private fun loadTransformationDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getTransformationDetails(transformationId).fold(
                ifLeft = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    _events.emit(TransformationEvent.ShowError(error.message))
                },
                ifRight = { data ->
                    _uiState.update {
                        it.copy(
                            transformation = data,
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _events.emit(TransformationEvent.NavigateBack)
        }
    }

    fun refresh() {
        loadTransformationDetails()
    }
}
