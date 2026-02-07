package com.example.kdragonball.android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kdragonball.android.R
import com.example.kdragonball.android.ui.component.CharacterCard
import com.example.kdragonball.android.ui.component.ErrorScreen
import com.example.kdragonball.android.ui.theme.AppTheme
import com.example.kdragonball.shared.feature.character.domain.model.Character
import com.example.kdragonball.shared.feature.character.presentation.viewmodel.CharacterEvent
import com.example.kdragonball.shared.feature.character.presentation.viewmodel.CharacterListUiState
import com.example.kdragonball.shared.feature.character.presentation.viewmodel.CharacterListViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

/**
 * Stateful composable that manages the CharacterListScreen.
 * Collects state from ViewModel and hoists it to stateless composables.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    viewModel: CharacterListViewModel = koinViewModel()
) {
    // Collect state at the top level
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()
    // Hoist list state to preserve scroll position during navigation
    val listState = rememberLazyListState()

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CharacterEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is CharacterEvent.NavigateToDetail -> {
                    onNavigateToDetail(event.characterId)
                }
                else -> {}
            }
        }
    }

    // Load more when reaching end of list
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null &&
                lastVisibleItem.index >= uiState.filteredCharacters.size - 3 &&
                !uiState.isLoading &&
                uiState.hasMorePages
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadNextPage()
        }
    }

    // Delegate to stateless composable with hoisted state
    CharacterListContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        pullToRefreshState = pullToRefreshState,
        listState = listState,
        onRefresh = viewModel::refresh,
        onSearchQueryChange = viewModel::searchCharacters,
        onCharacterClick = viewModel::onCharacterClick,
        onNavigateBack = onNavigateBack
    )
}

/**
 * Stateless composable that displays the character list screen.
 * All state is passed in as parameters (state hoisting pattern).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterListContent(
    uiState: CharacterListUiState,
    snackbarHostState: SnackbarHostState,
    pullToRefreshState: PullToRefreshState,
    listState: LazyListState,
    onRefresh: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCharacterClick: (Int) -> Unit,
    onNavigateBack: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CharacterListTopBar(onNavigateBack = onNavigateBack)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading && uiState.characters.isEmpty(),
            onRefresh = onRefresh,
            state = pullToRefreshState,
            modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    // Error state with no data
                    uiState.error != null && uiState.characters.isEmpty() -> {
                        ErrorScreen(
                            message = uiState.error ?: "An error occurred",
                            onRetry = onRefresh
                        )
                    }
                    else -> {
                        // Search bar
                        SearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = onSearchQueryChange,
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )

                        // Content based on state
                        when {
                            uiState.isEmpty && !uiState.isLoading -> {
                                EmptyState(modifier = Modifier.fillMaxSize())
                            }
                            else -> {
                                CharacterList(
                                    characters = uiState.filteredCharacters,
                                    isLoading = uiState.isLoading,
                                    hasMorePages = uiState.hasMorePages,
                                    listState = listState,
                                    onCharacterClick = onCharacterClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Stateless top app bar composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterListTopBar(onNavigateBack: (() -> Unit)?, modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.characters_title),
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            }
        },
        colors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

/**
 * Stateless search bar composable.
 */
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.characters_search_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.action_search)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.action_clear)
                    )
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        colors =
        TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

/**
 * Stateless character list composable.
 * Receives all data as parameters instead of collecting from ViewModel.
 */
@Composable
private fun CharacterList(
    characters: List<Character>,
    isLoading: Boolean,
    hasMorePages: Boolean,
    listState: LazyListState,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = characters,
            key = { it.id }
        ) { character ->
            CharacterCard(
                character = character,
                onClick = { onCharacterClick(character.id) }
            )
        }

        // Loading indicator at the bottom
        if (isLoading && characters.isNotEmpty()) {
            item(key = "loading_indicator") {
                LoadingIndicator()
            }
        }
    }
}

/**
 * Stateless loading indicator composable.
 */
@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier =
        modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Stateless empty state composable.
 */
@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.characters_empty),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.characters_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Preview helpers
private val previewCharacters =
    listOf(
        Character(
            id = 1,
            name = "Goku",
            ki = "60,000,000",
            maxKi = "90 Septillion",
            race = "Saiyan",
            gender = "Male",
            description = "El protagonista de la serie.",
            image = "https://dragonball-api.com/characters/goku.webp",
            affiliation = "Z Fighter"
        ),
        Character(
            id = 2,
            name = "Vegeta",
            ki = "54,000,000",
            maxKi = "19.84 Septillion",
            race = "Saiyan",
            gender = "Male",
            description = "Príncipe de los Saiyans.",
            image = "https://dragonball-api.com/characters/vegeta.webp",
            affiliation = "Z Fighter"
        )
    )

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CharacterListContentPreview() {
    AppTheme {
        CharacterListContent(
            uiState =
            CharacterListUiState(
                characters = previewCharacters,
                isLoading = false
            ),
            snackbarHostState = SnackbarHostState(),
            pullToRefreshState = rememberPullToRefreshState(),
            listState = LazyListState(),
            onRefresh = {},
            onSearchQueryChange = {},
            onCharacterClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Character List - Loading")
@Composable
private fun CharacterListLoadingPreview() {
    AppTheme {
        LoadingIndicator()
    }
}

@Preview(showBackground = true, name = "Character List - Empty")
@Composable
private fun CharacterListEmptyPreview() {
    AppTheme {
        EmptyState()
    }
}
