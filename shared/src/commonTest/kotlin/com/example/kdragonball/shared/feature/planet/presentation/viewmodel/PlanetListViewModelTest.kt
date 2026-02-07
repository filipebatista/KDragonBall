package com.example.kdragonball.shared.feature.planet.presentation.viewmodel

import com.example.kdragonball.shared.core.testutil.TestBuilders
import com.example.kdragonball.shared.feature.planet.domain.FakePlanetRepository
import com.example.kdragonball.shared.feature.planet.domain.model.PlanetError
import com.example.kdragonball.shared.feature.planet.domain.usecase.GetPlanets
import com.example.kdragonball.shared.feature.planet.domain.usecase.SearchPlanets
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class PlanetListViewModelTest {
    private lateinit var repository: FakePlanetRepository
    private lateinit var getPlanets: GetPlanets
    private lateinit var searchPlanets: SearchPlanets
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakePlanetRepository()
        getPlanets = GetPlanets(repository)
        searchPlanets = SearchPlanets(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given planets exist when viewmodel initializes then loads planets`() = runTest {
        // Given
        val planets = TestBuilders.planetList(count = 5)
        repository.setPlanets(planets)

        // When
        val viewModel = PlanetListViewModel(getPlanets, searchPlanets)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals(5, state.planets.size)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `given no planets when viewmodel initializes then shows empty state`() = runTest {
        // Given
        repository.setPlanets(emptyList())

        // When
        val viewModel = PlanetListViewModel(getPlanets, searchPlanets)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.planets.isEmpty())
        assertTrue(state.isEmpty)
        assertFalse(state.isLoading)
    }

    @Test
    fun `given repository fails when viewmodel initializes then shows error`() = runTest {
        // Given
        repository.shouldFailWithError = PlanetError.NetworkError("Connection failed")

        // When
        val viewModel = PlanetListViewModel(getPlanets, searchPlanets)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("Connection failed", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `given planets loaded when loading next page then appends new planets`() = runTest {
        // Given
        val firstPage = TestBuilders.planetList(count = 5, startId = 1)
        val secondPage = TestBuilders.planetList(count = 5, startId = 6)
        repository.setPlanets(firstPage + secondPage)

        val viewModel = PlanetListViewModel(getPlanets, searchPlanets)
        advanceUntilIdle()

        // When
        viewModel.loadNextPage()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals(2, state.currentPage)
    }

    @Test
    fun `given search query when searching then filters planets`() = runTest {
        // Given
        val planets =
            listOf(
                TestBuilders.planet(id = 1, name = "Earth"),
                TestBuilders.planet(id = 2, name = "Namek"),
                TestBuilders.planet(id = 3, name = "Vegeta")
            )
        repository.setPlanets(planets)

        val viewModel = PlanetListViewModel(getPlanets, searchPlanets)
        advanceUntilIdle()

        // When
        viewModel.searchPlanets("Ea")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("Ea", state.searchQuery)
        assertTrue(state.planets.all { it.name.contains("Ea", ignoreCase = true) })
    }

    @Test
    fun `given blank search query when searching then reloads all planets`() = runTest {
        // Given
        val planets = TestBuilders.planetList(count = 5)
        repository.setPlanets(planets)

        val viewModel = PlanetListViewModel(getPlanets, searchPlanets)
        advanceUntilIdle()

        viewModel.searchPlanets("test")
        advanceUntilIdle()

        // When
        viewModel.searchPlanets("")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("", state.searchQuery)
        assertEquals(5, state.planets.size)
    }

    @Test
    fun `given viewmodel with data when refreshing then reloads from first page`() = runTest {
        // Given
        val planets = TestBuilders.planetList(count = 5)
        repository.setPlanets(planets)

        val viewModel = PlanetListViewModel(getPlanets, searchPlanets)
        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals(1, state.currentPage)
    }

    @Test
    fun `given loading in progress when loading next page then does not load`() = runTest {
        // Given
        repository.setPlanets(TestBuilders.planetList(count = 5))
        val viewModel = PlanetListViewModel(getPlanets, searchPlanets)
        // Don't advance - keep in loading state

        // When
        val initialCalls = repository.getPlanetsCalls.size
        viewModel.loadNextPage()

        // Then - should not add new calls while loading
        assertEquals(initialCalls, repository.getPlanetsCalls.size)
    }
}
