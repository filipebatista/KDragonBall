package com.example.kdragonball.shared.feature.character.data.datasource

import com.example.kdragonball.shared.core.model.PaginatedResponse
import com.example.kdragonball.shared.feature.character.domain.model.Character

/**
 * Data source interface for fetching character data from the API
 */
interface CharacterDataSource {
    /**
     * Fetch paginated list of characters from the API
     */
    suspend fun fetchCharacters(page: Int, limit: Int): PaginatedResponse<Character>

    /**
     * Fetch a specific character by ID
     */
    suspend fun fetchCharacter(characterId: Int): Character

    /**
     * Search characters by name
     */
    suspend fun searchCharacters(query: String): List<Character>
}
