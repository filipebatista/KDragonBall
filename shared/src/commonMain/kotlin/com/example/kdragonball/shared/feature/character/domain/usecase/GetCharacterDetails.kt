package com.example.kdragonball.shared.feature.character.domain.usecase

import arrow.core.Either
import com.example.kdragonball.shared.feature.character.domain.model.Character
import com.example.kdragonball.shared.feature.character.domain.model.CharacterError
import com.example.kdragonball.shared.feature.character.domain.repository.CharacterRepository

/**
 * Use case for retrieving details of a specific Dragon Ball character
 */
class GetCharacterDetails(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(characterId: Int): Either<CharacterError, Character> {
        return repository.getCharacter(characterId)
    }
}
