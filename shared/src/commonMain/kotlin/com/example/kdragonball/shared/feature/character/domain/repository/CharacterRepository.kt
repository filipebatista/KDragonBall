package com.example.kdragonball.shared.feature.character.domain.repository

import arrow.core.Either
import com.example.kdragonball.shared.feature.character.domain.model.Character
import com.example.kdragonball.shared.feature.character.domain.model.CharacterError
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing Dragon Ball character data
 */
interface CharacterRepository {
    /**
     * Observe all characters with real-time updates
     */
    fun observeCharacters(): Flow<List<Character>>

    /**
     * Observe a specific character by ID with real-time updates
     */
    fun observeCharacter(characterId: Int): Flow<Character?>

    /**
     * Get paginated list of characters
     * @param page Page number (starting from 1)
     * @param limit Number of items per page
     */
    suspend fun getCharacters(
        page: Int = 1,
        limit: Int = 10
    ): Either<CharacterError, List<Character>>

    /**
     * Get a specific character by ID
     */
    suspend fun getCharacter(characterId: Int): Either<CharacterError, Character>

    /**
     * Search characters by name
     */
    suspend fun searchCharacters(query: String): Either<CharacterError, List<Character>>

    /**
     * Get characters filtered by affiliation (heroes, villains, etc.)
     */
    suspend fun getCharactersByAffiliation(
        affiliation: String
    ): Either<CharacterError, List<Character>>
}
