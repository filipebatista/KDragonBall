package com.example.kdragonball.shared.feature.character.presentation.viewmodel

import com.example.kdragonball.shared.core.testutil.TestBuilders
import com.example.kdragonball.shared.feature.character.domain.FakeCharacterRepository
import com.example.kdragonball.shared.feature.character.domain.model.CharacterError
import com.example.kdragonball.shared.feature.character.domain.usecase.GetCharacterDetails
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {
    private lateinit var repository: FakeCharacterRepository
    private lateinit var getCharacterDetails: GetCharacterDetails
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeCharacterRepository()
        getCharacterDetails = GetCharacterDetails(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given character exists when viewmodel initializes then loads character details`() =
        runTest {
            // Given
            val character = TestBuilders.character(id = 1, name = "Goku")
            repository.setCharacters(listOf(character))

            // When
            val viewModel = CharacterDetailViewModel(getCharacterDetails, characterId = 1)
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.first()
            assertNotNull(state.character)
            assertEquals("Goku", state.character?.name)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }

    @Test
    fun `given character does not exist when viewmodel initializes then shows error`() = runTest {
        // Given
        repository.setCharacters(emptyList())

        // When
        val viewModel = CharacterDetailViewModel(getCharacterDetails, characterId = 999)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertNull(state.character)
        assertNotNull(state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `given repository fails when viewmodel initializes then shows error`() = runTest {
        // Given
        repository.shouldFailWithError = CharacterError.NetworkError("Network unavailable")

        // When
        val viewModel = CharacterDetailViewModel(getCharacterDetails, characterId = 1)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("Network unavailable", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `given character loaded when refreshing then reloads character details`() = runTest {
        // Given
        val character = TestBuilders.character(id = 1, name = "Goku")
        repository.setCharacters(listOf(character))

        val viewModel = CharacterDetailViewModel(getCharacterDetails, characterId = 1)
        advanceUntilIdle()

        // Update the character name
        val updatedCharacter = TestBuilders.character(id = 1, name = "Goku (Updated)")
        repository.setCharacters(listOf(updatedCharacter))

        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("Goku (Updated)", state.character?.name)
    }

    @Test
    fun `given character with transformations when loaded then includes transformations`() =
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
            val viewModel = CharacterDetailViewModel(getCharacterDetails, characterId = 1)
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.first()
            assertEquals(2, state.character?.transformations?.size)
        }

    @Test
    fun `given character with origin planet when loaded then includes origin planet`() = runTest {
        // Given
        val originPlanet = TestBuilders.originPlanet(id = 1, name = "Vegeta")
        val character =
            TestBuilders.character(
                id = 1,
                name = "Goku",
                originPlanet = originPlanet
            )
        repository.setCharacters(listOf(character))

        // When
        val viewModel = CharacterDetailViewModel(getCharacterDetails, characterId = 1)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("Vegeta", state.character?.originPlanet?.name)
    }
}
