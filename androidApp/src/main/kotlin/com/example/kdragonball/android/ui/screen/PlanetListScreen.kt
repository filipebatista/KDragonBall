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
import com.example.kdragonball.android.ui.component.ErrorScreen
import com.example.kdragonball.android.ui.component.PlanetCard
import com.example.kdragonball.android.ui.theme.AppTheme
import com.example.kdragonball.shared.feature.planet.domain.model.Planet
import com.example.kdragonball.shared.feature.planet.presentation.viewmodel.PlanetEvent
import com.example.kdragonball.shared.feature.planet.presentation.viewmodel.PlanetListUiState
import com.example.kdragonball.shared.feature.planet.presentation.viewmodel.PlanetListViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PlanetListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is PlanetEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is PlanetEvent.NavigateToDetail -> {
                    onNavigateToDetail(event.planetId)
                }
                else -> {}
            }
        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null &&
                lastVisibleItem.index >= uiState.filteredPlanets.size - 3 &&
                !uiState.isLoading &&
                uiState.hasMorePages
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadNextPage()
        }
    }

    PlanetListContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        pullToRefreshState = pullToRefreshState,
        listState = listState,
        onRefresh = viewModel::refresh,
        onSearchQueryChange = viewModel::searchPlanets,
        onPlanetClick = viewModel::onPlanetClick,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanetListContent(
    uiState: PlanetListUiState,
    snackbarHostState: SnackbarHostState,
    pullToRefreshState: PullToRefreshState,
    listState: LazyListState,
    onRefresh: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onPlanetClick: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            PlanetListTopBar(onNavigateBack = onNavigateBack)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading && uiState.planets.isEmpty(),
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
                    uiState.error != null && uiState.planets.isEmpty() -> {
                        ErrorScreen(
                            message = uiState.error ?: "An error occurred",
                            onRetry = onRefresh
                        )
                    }
                    else -> {
                        SearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = onSearchQueryChange,
                            placeholder = stringResource(R.string.planets_search_placeholder),
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )

                        when {
                            uiState.isEmpty && !uiState.isLoading -> {
                                EmptyState(
                                    message = stringResource(R.string.planets_empty),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else -> {
                                PlanetList(
                                    planets = uiState.filteredPlanets,
                                    isLoading = uiState.isLoading,
                                    listState = listState,
                                    onPlanetClick = onPlanetClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanetListTopBar(onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.planets_title),
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back)
                )
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

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(placeholder) },
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

@Composable
private fun PlanetList(
    planets: List<Planet>,
    isLoading: Boolean,
    listState: LazyListState,
    onPlanetClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = planets,
            key = { it.id }
        ) { planet ->
            PlanetCard(
                planet = planet,
                onClick = { onPlanetClick(planet.id) }
            )
        }

        if (isLoading && planets.isNotEmpty()) {
            item(key = "loading_indicator") {
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.planets_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Preview helpers
private val previewPlanets =
    listOf(
        Planet(
            id = 1,
            name = "Earth",
            isDestroyed = false,
            description = "Home planet of humans and the Z Fighters.",
            image = "https://dragonball-api.com/planets/earth.webp"
        ),
        Planet(
            id = 2,
            name = "Namek",
            isDestroyed = true,
            description = "Home planet of the Namekians.",
            image = "https://dragonball-api.com/planets/namek.webp"
        )
    )

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun PlanetListContentPreview() {
    AppTheme {
        PlanetListContent(
            uiState =
            PlanetListUiState(
                planets = previewPlanets,
                isLoading = false
            ),
            snackbarHostState = SnackbarHostState(),
            pullToRefreshState = rememberPullToRefreshState(),
            listState = LazyListState(),
            onRefresh = {},
            onSearchQueryChange = {},
            onPlanetClick = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Planet List - Empty")
@Composable
private fun PlanetListEmptyPreview() {
    AppTheme {
        EmptyState(message = "No planets found")
    }
}
