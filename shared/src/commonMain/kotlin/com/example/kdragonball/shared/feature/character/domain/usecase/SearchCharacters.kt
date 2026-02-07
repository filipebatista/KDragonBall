package com.example.kdragonball.shared.feature.character.domain.usecase

import arrow.core.Either
import com.example.kdragonball.shared.feature.character.domain.model.Character
import com.example.kdragonball.shared.feature.character.domain.model.CharacterError
import com.example.kdragonball.shared.feature.character.domain.repository.CharacterRepository

/**
 * Use case for searching Dragon Ball characters by name
 */
class SearchCharacters(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(query: String): Either<CharacterError, List<Character>> {
        return repository.searchCharacters(query)
    }
}
