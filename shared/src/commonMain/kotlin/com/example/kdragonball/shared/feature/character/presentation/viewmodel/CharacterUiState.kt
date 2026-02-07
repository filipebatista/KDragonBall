package com.example.kdragonball.shared.feature.character.presentation.viewmodel

import com.example.kdragonball.shared.feature.character.domain.model.Character

/**
 * UI state for the character list screen
 */
data class CharacterListUiState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true
) {
    val isEmpty: Boolean
        get() = characters.isEmpty() && !isLoading

    val filteredCharacters: List<Character>
        get() =
            if (searchQuery.isBlank()) {
                characters
            } else {
                characters.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }
}

/**
 * UI state for the character detail screen
 */
data class CharacterDetailUiState(
    val character: Character? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Events for character screens
 */
sealed class CharacterEvent {
    data class ShowError(val message: String) : CharacterEvent()

    data class NavigateToDetail(val characterId: Int) : CharacterEvent()

    data object NavigateBack : CharacterEvent()
}
