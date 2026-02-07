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

class SearchCharactersTest {
    private lateinit var repository: FakeCharacterRepository
    private lateinit var searchCharacters: SearchCharacters

    @BeforeTest
    fun setUp() {
        repository = FakeCharacterRepository()
        searchCharacters = SearchCharacters(repository)
    }

    @Test
    fun `given characters exist when searching with matching query then returns matching characters`() =
        runTest {
            // Given
            val characters =
                listOf(
                    TestBuilders.character(id = 1, name = "Goku"),
                    TestBuilders.character(id = 2, name = "Gohan"),
                    TestBuilders.character(id = 3, name = "Vegeta")
                )
            repository.setCharacters(characters)

            // When
            val result = searchCharacters(query = "Go")

            // Then
            assertTrue(result.isRight())
            val foundCharacters = result.getOrElse { emptyList() }
            assertEquals(2, foundCharacters.size)
            assertTrue(foundCharacters.any { it.name == "Goku" })
            assertTrue(foundCharacters.any { it.name == "Gohan" })
        }

    @Test
    fun `given characters exist when searching with no matching query then returns empty list`() =
        runTest {
            // Given
            val characters =
                listOf(
                    TestBuilders.character(id = 1, name = "Goku"),
                    TestBuilders.character(id = 2, name = "Vegeta")
                )
            repository.setCharacters(characters)

            // When
            val result = searchCharacters(query = "Piccolo")

            // Then
            assertTrue(result.isRight())
            assertEquals(0, result.getOrElse { emptyList() }.size)
        }

    @Test
    fun `given characters exist when searching with case insensitive query then returns matching characters`() =
        runTest {
            // Given
            val characters =
                listOf(
                    TestBuilders.character(id = 1, name = "Goku"),
                    TestBuilders.character(id = 2, name = "VEGETA")
                )
            repository.setCharacters(characters)

            // When
            val result = searchCharacters(query = "vegeta")

            // Then
            assertTrue(result.isRight())
            val data = result.getOrElse { emptyList() }
            assertEquals(1, data.size)
            assertEquals("VEGETA", data.first().name)
        }

    @Test
    fun `given repository configured to return specific result when searching then returns that result`() =
        runTest {
            // Given
            val expectedResults = listOf(TestBuilders.character(id = 99, name = "Custom Result"))
            repository.searchCharactersResult = expectedResults.right()

            // When
            val result = searchCharacters(query = "anything")

            // Then
            assertTrue(result.isRight())
            assertEquals("Custom Result", result.getOrElse { emptyList() }.first().name)
        }

    @Test
    fun `given repository fails when searching then returns error`() = runTest {
        // Given
        repository.shouldFailWithError = CharacterError.NetworkError("Search failed")

        // When
        val result = searchCharacters(query = "Goku")

        // Then
        assertTrue(result.isLeft())
        result.onLeft { error ->
            assertTrue(error is CharacterError.NetworkError)
        }
    }

    @Test
    fun `given empty query when searching then passes empty query to repository`() = runTest {
        // Given
        repository.setCharacters(TestBuilders.characterList(count = 3))

        // When
        searchCharacters(query = "")

        // Then
        assertEquals(1, repository.searchCharactersCalls.size)
        assertEquals("", repository.searchCharactersCalls.first())
    }

    @Test
    fun `given search query when searching then passes exact query to repository`() = runTest {
        // Given
        repository.setCharacters(emptyList())

        // When
        searchCharacters(query = "Super Saiyan")

        // Then
        assertEquals(1, repository.searchCharactersCalls.size)
        assertEquals("Super Saiyan", repository.searchCharactersCalls.first())
    }
}
