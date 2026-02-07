package com.example.kdragonball.shared.feature.character.domain.usecase

import arrow.core.getOrElse
import arrow.core.right
import com.example.kdragonball.shared.core.testutil.TestBuilders
import com.example.kdragonball.shared.feature.character.domain.FakeCharacterRepository
import com.example.kdragonball.shared.feature.character.domain.model.CharacterError
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class GetCharactersTest {
    private lateinit var repository: FakeCharacterRepository
    private lateinit var getCharacters: GetCharacters

    @BeforeTest
    fun setUp() {
        repository = FakeCharacterRepository()
        getCharacters = GetCharacters(repository)
    }

    @Test
    fun `given characters exist when getting characters then returns success with character list`() =
        runTest {
            // Given
            val characters = TestBuilders.characterList(count = 5)
            repository.setCharacters(characters)

            // When
            val result = getCharacters(page = 1, limit = 10)

            // Then
            assertTrue(result.isRight())
            assertEquals(5, result.getOrElse { emptyList() }.size)
        }

    @Test
    fun `given no characters exist when getting characters then returns success with empty list`() =
        runTest {
            // Given
            repository.clearCharacters()

            // When
            val result = getCharacters(page = 1, limit = 10)

            // Then
            assertTrue(result.isRight())
            assertEquals(0, result.getOrElse { emptyList() }.size)
        }

    @Test
    fun `given characters exist when getting second page then returns correct subset`() = runTest {
        // Given
        val characters = TestBuilders.characterList(count = 15, startId = 1)
        repository.setCharacters(characters)

        // When
        val result = getCharacters(page = 2, limit = 10)

        // Then
        assertTrue(result.isRight())
        assertEquals(5, result.getOrElse { emptyList() }.size)
    }

    @Test
    fun `given repository configured to return specific result when getting characters then returns that result`() =
        runTest {
            // Given
            val expectedCharacters = listOf(TestBuilders.character(id = 99, name = "Custom"))
            repository.getCharactersResult = expectedCharacters.right()

            // When
            val result = getCharacters(page = 1, limit = 10)

            // Then
            assertTrue(result.isRight())
            assertEquals("Custom", result.getOrElse { emptyList() }.first().name)
        }

    @Test
    fun `given repository fails when getting characters then returns error`() = runTest {
        // Given
        repository.shouldFailWithError = CharacterError.NetworkError("Connection failed")

        // When
        val result = getCharacters(page = 1, limit = 10)

        // Then
        assertTrue(result.isLeft())
        result.onLeft { error ->
            assertTrue(error is CharacterError.NetworkError)
        }
    }

    @Test
    fun `given default parameters when getting characters then uses page 1 and limit 10`() =
        runTest {
            // Given
            repository.setCharacters(TestBuilders.characterList(count = 5))

            // When
            getCharacters()

            // Then
            assertEquals(1, repository.getCharactersCalls.size)
            assertEquals(1 to 10, repository.getCharactersCalls.first())
        }

    @Test
    fun `given custom pagination when getting characters then passes correct parameters to repository`() =
        runTest {
            // Given
            repository.setCharacters(TestBuilders.characterList(count = 50))

            // When
            getCharacters(page = 3, limit = 20)

            // Then
            assertEquals(1, repository.getCharactersCalls.size)
            assertEquals(3 to 20, repository.getCharactersCalls.first())
        }
}
