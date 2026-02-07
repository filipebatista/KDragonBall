package com.example.kdragonball.shared.feature.character.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.kdragonball.shared.feature.character.data.datasource.CharacterDataSource
import com.example.kdragonball.shared.feature.character.domain.model.Character
import com.example.kdragonball.shared.feature.character.domain.model.CharacterError
import com.example.kdragonball.shared.feature.character.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Implementation of CharacterRepository using the API data source
 */
class CharacterRepositoryImpl(
    private val dataSource: CharacterDataSource
) : CharacterRepository {
    // Cache for characters
    private val charactersCache = MutableStateFlow<Map<Int, Character>>(emptyMap())

    override fun observeCharacters(): Flow<List<Character>> {
        return charactersCache.map { it.values.toList() }
    }

    override fun observeCharacter(characterId: Int): Flow<Character?> {
        return charactersCache.map { it[characterId] }
    }

    override suspend fun getCharacters(
        page: Int,
        limit: Int
    ): Either<CharacterError, List<Character>> {
        return try {
            val response = dataSource.fetchCharacters(page, limit)
            val characters = response.items

            // Update cache
            charactersCache.value = charactersCache.value + characters.associateBy { it.id }

            characters.right()
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun getCharacter(characterId: Int): Either<CharacterError, Character> {
        return try {
            // Check cache first
            charactersCache.value[characterId]?.let {
                return it.right()
            }

            // Fetch from API
            val character = dataSource.fetchCharacter(characterId)

            // Update cache
            charactersCache.value = charactersCache.value + (character.id to character)

            character.right()
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun searchCharacters(query: String): Either<CharacterError, List<Character>> {
        return try {
            if (query.isBlank()) {
                return charactersCache.value.values.toList().right()
            }

            val characters = dataSource.searchCharacters(query)

            // Update cache
            charactersCache.value = charactersCache.value + characters.associateBy { it.id }

            characters.right()
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun getCharactersByAffiliation(
        affiliation: String
    ): Either<CharacterError, List<Character>> {
        return try {
            // Fetch all characters and filter by affiliation
            val response = dataSource.fetchCharacters(page = 1, limit = 100)
            val characters =
                response.items.filter {
                    it.affiliation.contains(affiliation, ignoreCase = true)
                }

            characters.right()
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun <T> handleException(e: Exception): Either<CharacterError, T> {
        return when {
            e.message?.contains("404") == true -> {
                CharacterError.NotFound("Character not found").left()
            }
            e.message?.contains("network") == true || e.message?.contains("timeout") == true -> {
                CharacterError.NetworkError("Network error: ${e.message}").left()
            }
            else -> {
                CharacterError.UnknownError("Unknown error: ${e.message}").left()
            }
        }
    }
}
