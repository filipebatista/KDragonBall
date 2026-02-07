package com.example.kdragonball.shared.feature.character.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kdragonball.shared.feature.character.domain.usecase.GetCharacterDetails
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the character detail screen
 */
class CharacterDetailViewModel(
    private val getCharacterDetails: GetCharacterDetails,
    private val characterId: Int
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharacterDetailUiState())
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CharacterEvent>()
    val events = _events.asSharedFlow()

    init {
        loadCharacterDetails()
    }

    private fun loadCharacterDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getCharacterDetails(characterId).fold(
                ifLeft = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    _events.emit(CharacterEvent.ShowError(error.message))
                },
                ifRight = { data ->
                    _uiState.update {
                        it.copy(
                            character = data,
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _events.emit(CharacterEvent.NavigateBack)
        }
    }

    fun refresh() {
        loadCharacterDetails()
    }
}
