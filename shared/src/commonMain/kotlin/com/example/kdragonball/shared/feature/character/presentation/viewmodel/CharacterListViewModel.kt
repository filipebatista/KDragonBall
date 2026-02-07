package com.example.kdragonball.shared.feature.character.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kdragonball.shared.feature.character.domain.usecase.GetCharacters
import com.example.kdragonball.shared.feature.character.domain.usecase.SearchCharacters
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the character list screen
 */
class CharacterListViewModel(
    private val getCharacters: GetCharacters,
    private val searchCharactersUseCase: SearchCharacters
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharacterListUiState())
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CharacterEvent>()
    val events = _events.asSharedFlow()

    init {
        loadCharacters()
    }

    fun loadCharacters(page: Int = 1) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getCharacters(page = page, limit = 20).fold(
                ifLeft = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    _events.emit(CharacterEvent.ShowError(error.message))
                },
                ifRight = { data ->
                    _uiState.update {
                        it.copy(
                            characters = if (page == 1) data else it.characters + data,
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
            loadCharacters(_uiState.value.currentPage + 1)
        }
    }

    fun searchCharacters(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            // Reload the full list when search is cleared
            loadCharacters(page = 1)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            searchCharactersUseCase(query).fold(
                ifLeft = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                    _events.emit(CharacterEvent.ShowError(error.message))
                },
                ifRight = { data ->
                    _uiState.update {
                        it.copy(
                            characters = data,
                            isLoading = false,
                            hasMorePages = false
                        )
                    }
                }
            )
        }
    }

    fun onCharacterClick(characterId: Int) {
        viewModelScope.launch {
            _events.emit(CharacterEvent.NavigateToDetail(characterId))
        }
    }

    fun refresh() {
        loadCharacters(page = 1)
    }
}
