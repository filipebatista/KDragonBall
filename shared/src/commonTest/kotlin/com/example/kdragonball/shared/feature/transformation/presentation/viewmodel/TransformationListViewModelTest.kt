package com.example.kdragonball.shared.feature.transformation.presentation.viewmodel

import com.example.kdragonball.shared.core.testutil.TestBuilders
import com.example.kdragonball.shared.feature.transformation.domain.FakeTransformationRepository
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationError
import com.example.kdragonball.shared.feature.transformation.domain.usecase.GetTransformations
import com.example.kdragonball.shared.feature.transformation.domain.usecase.SearchTransformations
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TransformationListViewModelTest {
    private lateinit var repository: FakeTransformationRepository
    private lateinit var getTransformations: GetTransformations
    private lateinit var searchTransformations: SearchTransformations
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTransformationRepository()
        getTransformations = GetTransformations(repository)
        searchTransformations = SearchTransformations(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given transformations exist when viewmodel initializes then loads transformations`() =
        runTest {
            // Given
            val transformations = TestBuilders.transformationDetailList(count = 5)
            repository.setTransformations(transformations)

            // When
            val viewModel = TransformationListViewModel(getTransformations, searchTransformations)
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.first()
            assertEquals(5, state.transformations.size)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }

    @Test
    fun `given no transformations when viewmodel initializes then shows empty state`() = runTest {
        // Given
        repository.setTransformations(emptyList())

        // When
        val viewModel = TransformationListViewModel(getTransformations, searchTransformations)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state.transformations.isEmpty())
        assertTrue(state.isEmpty)
        assertFalse(state.isLoading)
    }

    @Test
    fun `given repository fails when viewmodel initializes then shows error`() = runTest {
        // Given
        repository.shouldFailWithError = TransformationError.NetworkError("Connection failed")

        // When
        val viewModel = TransformationListViewModel(getTransformations, searchTransformations)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("Connection failed", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `given search query when searching then filters transformations`() = runTest {
        // Given
        val transformations =
            listOf(
                TestBuilders.transformationDetail(id = 1, name = "Super Saiyan"),
                TestBuilders.transformationDetail(id = 2, name = "Super Saiyan 2"),
                TestBuilders.transformationDetail(id = 3, name = "Ultra Instinct")
            )
        repository.setTransformations(transformations)

        val viewModel = TransformationListViewModel(getTransformations, searchTransformations)
        advanceUntilIdle()

        // When
        viewModel.searchTransformations("Super")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("Super", state.searchQuery)
        assertTrue(state.transformations.all { it.name.contains("Super", ignoreCase = true) })
    }

    @Test
    fun `given blank search query when searching then reloads all transformations`() = runTest {
        // Given
        val transformations = TestBuilders.transformationDetailList(count = 5)
        repository.setTransformations(transformations)

        val viewModel = TransformationListViewModel(getTransformations, searchTransformations)
        advanceUntilIdle()

        viewModel.searchTransformations("test")
        advanceUntilIdle()

        // When
        viewModel.searchTransformations("")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals("", state.searchQuery)
        assertEquals(5, state.transformations.size)
    }

    @Test
    fun `given viewmodel with data when refreshing then reloads transformations`() = runTest {
        // Given
        val transformations = TestBuilders.transformationDetailList(count = 5)
        repository.setTransformations(transformations)

        val viewModel = TransformationListViewModel(getTransformations, searchTransformations)
        advanceUntilIdle()

        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertEquals(5, state.transformations.size)
        assertFalse(state.isLoading)
    }

    @Test
    fun `given transformation click when onTransformationClick called then emits navigation event`() =
        runTest {
            // Given
            repository.setTransformations(TestBuilders.transformationDetailList(count = 1))
            val viewModel = TransformationListViewModel(getTransformations, searchTransformations)
            advanceUntilIdle()

            // Start collecting events before triggering the action
            val eventDeferred =
                async {
                    viewModel.events.first()
                }

            // When
            viewModel.onTransformationClick(1)
            advanceUntilIdle()

            // Then
            val event = eventDeferred.await()
            assertTrue(event is TransformationEvent.NavigateToDetail)
            assertEquals(1, (event as TransformationEvent.NavigateToDetail).transformationId)
        }
}
