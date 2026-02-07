package com.example.kdragonball.shared.feature.character.domain.usecase

import arrow.core.right
import com.example.kdragonball.shared.core.testutil.TestBuilders
import com.example.kdragonball.shared.feature.character.domain.FakeCharacterRepository
import com.example.kdragonball.shared.feature.character.domain.model.CharacterError
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class GetCharacterDetailsTest {
    private lateinit var repository: FakeCharacterRepository
    private lateinit var getCharacterDetails: GetCharacterDetails

    @BeforeTest
    fun setUp() {
        repository = FakeCharacterRepository()
        getCharacterDetails = GetCharacterDetails(repository)
    }

    @Test
    fun `given character exists when getting character details then returns success with character`() =
        runTest {
            // Given
            val character = TestBuilders.character(id = 1, name = "Goku")
            repository.setCharacters(listOf(character))

            // When
            val result = getCharacterDetails(characterId = 1)

            // Then
            assertTrue(result.isRight())
            result.onRight { data ->
                assertEquals("Goku", data.name)
            }
        }

    @Test
    fun `given character does not exist when getting character details then returns not found error`() =
        runTest {
            // Given
            repository.clearCharacters()

            // When
            val result = getCharacterDetails(characterId = 999)

            // Then
            assertTrue(result.isLeft())
            result.onLeft { error ->
                assertTrue(error is CharacterError.NotFound)
            }
        }

    @Test
    fun `given repository configured to return specific character when getting details then returns that character`() =
        runTest {
            // Given
            val expectedCharacter = TestBuilders.character(
                id = 42,
                name = "Vegeta",
                race = "Saiyan Prince"
            )
            repository.getCharacterResult = expectedCharacter.right()

            // When
            val result = getCharacterDetails(characterId = 42)

            // Then
            assertTrue(result.isRight())
            result.onRight { character ->
                assertEquals("Vegeta", character.name)
                assertEquals("Saiyan Prince", character.race)
            }
        }

    @Test
    fun `given repository fails with network error when getting character details then returns network error`() =
        runTest {
            // Given
            repository.shouldFailWithError = CharacterError.NetworkError("Server unreachable")

            // When
            val result = getCharacterDetails(characterId = 1)

            // Then
            assertTrue(result.isLeft())
            result.onLeft { error ->
                assertTrue(error is CharacterError.NetworkError)
                assertEquals("Server unreachable", error.message)
            }
        }

    @Test
    fun `given character with transformations when getting details then returns character with transformations`() =
        runTest {
            // Given
            val transformations =
                listOf(
                    TestBuilders.transformation(id = 1, name = "Super Saiyan"),
                    TestBuilders.transformation(id = 2, name = "Super Saiyan 2")
                )
            val character =
                TestBuilders.character(
                    id = 1,
                    name = "Goku",
                    transformations = transformations
                )
            repository.setCharacters(listOf(character))

            // When
            val result = getCharacterDetails(characterId = 1)

            // Then
            assertTrue(result.isRight())
            result.onRight { returnedCharacter ->
                assertEquals(2, returnedCharacter.transformations.size)
                assertEquals("Super Saiyan", returnedCharacter.transformations[0].name)
            }
        }

    @Test
    fun `given character id when getting details then passes correct id to repository`() = runTest {
        // Given
        repository.setCharacters(listOf(TestBuilders.character(id = 123)))

        // When
        getCharacterDetails(characterId = 123)

        // Then
        assertEquals(1, repository.getCharacterCalls.size)
        assertEquals(123, repository.getCharacterCalls.first())
    }
}
