package com.example.kdragonball.shared.feature.character.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.kdragonball.shared.feature.character.domain.model.Character
import com.example.kdragonball.shared.feature.character.domain.model.CharacterError
import com.example.kdragonball.shared.feature.character.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of CharacterRepository for testing.
 * Allows configuring responses and tracking method calls.
 */
class FakeCharacterRepository : CharacterRepository {
    private val charactersStore = MutableStateFlow<Map<Int, Character>>(emptyMap())

    // Configuration for controlling behavior
    var getCharactersResult: Either<CharacterError, List<Character>>? = null
    var getCharacterResult: Either<CharacterError, Character>? = null
    var searchCharactersResult: Either<CharacterError, List<Character>>? = null
    var getCharactersByAffiliationResult: Either<CharacterError, List<Character>>? = null

    // Error simulation
    var shouldFailWithError: CharacterError? = null

    // Call tracking
    var getCharactersCalls = mutableListOf<Pair<Int, Int>>() // page, limit
    var getCharacterCalls = mutableListOf<Int>() // characterId
    var searchCharactersCalls = mutableListOf<String>() // query

    /**
     * Prepopulate the store with characters for testing
     */
    fun setCharacters(characters: List<Character>) {
        charactersStore.value = characters.associateBy { it.id }
    }

    fun clearCharacters() {
        charactersStore.value = emptyMap()
    }

    fun reset() {
        charactersStore.value = emptyMap()
        getCharactersResult = null
        getCharacterResult = null
        searchCharactersResult = null
        getCharactersByAffiliationResult = null
        shouldFailWithError = null
        getCharactersCalls.clear()
        getCharacterCalls.clear()
        searchCharactersCalls.clear()
    }

    override fun observeCharacters(): Flow<List<Character>> {
        return charactersStore.map { it.values.toList() }
    }

    override fun observeCharacter(characterId: Int): Flow<Character?> {
        return charactersStore.map { it[characterId] }
    }

    override suspend fun getCharacters(
        page: Int,
        limit: Int
    ): Either<CharacterError, List<Character>> {
        getCharactersCalls.add(page to limit)

        shouldFailWithError?.let { error ->
            return error.left()
        }

        getCharactersResult?.let { return it }

        // Default behavior: return characters from store with pagination
        val allCharacters = charactersStore.value.values.toList()
        val startIndex = (page - 1) * limit
        val endIndex = minOf(startIndex + limit, allCharacters.size)

        return if (startIndex < allCharacters.size) {
            allCharacters.subList(startIndex, endIndex).right()
        } else {
            emptyList<Character>().right()
        }
    }

    override suspend fun getCharacter(characterId: Int): Either<CharacterError, Character> {
        getCharacterCalls.add(characterId)

        shouldFailWithError?.let { error ->
            return error.left()
        }

        getCharacterResult?.let { return it }

        // Default behavior: return character from store
        val character = charactersStore.value[characterId]
        return if (character != null) {
            character.right()
        } else {
            CharacterError.NotFound("Character with id $characterId not found").left()
        }
    }

    override suspend fun searchCharacters(query: String): Either<CharacterError, List<Character>> {
        searchCharactersCalls.add(query)

        shouldFailWithError?.let { error ->
            return error.left()
        }

        searchCharactersResult?.let { return it }

        // Default behavior: filter characters by name
        val filteredCharacters =
            charactersStore.value.values
                .filter { it.name.contains(query, ignoreCase = true) }
        return filteredCharacters.right()
    }

    override suspend fun getCharactersByAffiliation(
        affiliation: String
    ): Either<CharacterError, List<Character>> {
        shouldFailWithError?.let { error ->
            return error.left()
        }

        getCharactersByAffiliationResult?.let { return it }

        // Default behavior: filter by affiliation
        val filteredCharacters =
            charactersStore.value.values
                .filter { it.affiliation.contains(affiliation, ignoreCase = true) }
        return filteredCharacters.right()
    }
}
