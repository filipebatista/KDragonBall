package com.example.kdragonball.shared.feature.character.domain.usecase

import arrow.core.Either
import com.example.kdragonball.shared.feature.character.domain.model.Character
import com.example.kdragonball.shared.feature.character.domain.model.CharacterError
import com.example.kdragonball.shared.feature.character.domain.repository.CharacterRepository

/**
 * Use case for retrieving a list of Dragon Ball characters
 */
class GetCharacters(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 10
    ): Either<CharacterError, List<Character>> {
        return repository.getCharacters(page, limit)
    }
}
