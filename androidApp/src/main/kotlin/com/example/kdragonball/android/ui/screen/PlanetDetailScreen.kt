package com.example.kdragonball.android.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kdragonball.android.R
import com.example.kdragonball.android.ui.component.ErrorScreen
import com.example.kdragonball.android.ui.theme.AppTheme
import com.example.kdragonball.android.ui.theme.DragonBallBlue
import com.example.kdragonball.shared.feature.planet.domain.model.Planet
import com.example.kdragonball.shared.feature.planet.presentation.viewmodel.PlanetDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetDetailScreen(
    planetId: Int,
    onNavigateBack: () -> Unit,
    viewModel: PlanetDetailViewModel =
        koinViewModel(
            key = "planet_$planetId"
        ) { parametersOf(planetId) }
) {
    val uiState by viewModel.uiState.collectAsState()
    var fullscreenImageUrl by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = fullscreenImageUrl != null) {
        fullscreenImageUrl = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.planet?.name
                                ?: stringResource(R.string.detail_planet),
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
        ) { paddingValues ->
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.planet != null -> {
                        PlanetDetailContent(
                            planet = uiState.planet!!,
                            onImageClick = { imageUrl -> fullscreenImageUrl = imageUrl },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    uiState.error != null -> {
                        ErrorScreen(
                            message = uiState.error!!,
                            onRetry = { viewModel.refresh() }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = fullscreenImageUrl != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FullscreenImageViewer(
                imageUrl = fullscreenImageUrl ?: "",
                onDismiss = { fullscreenImageUrl = null }
            )
        }
    }
}

@Composable
private fun PlanetDetailContent(
    planet: Planet,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
        modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Planet image
        if (planet.image.isNotEmpty()) {
            AsyncImage(
                model = planet.image,
                contentDescription = planet.name,
                modifier =
                Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(DragonBallBlue.copy(alpha = 0.2f))
                    .clickable { onImageClick(planet.image) },
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier =
                Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(DragonBallBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = planet.name.take(2).uppercase(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = DragonBallBlue
                )
            }
        }

        // Basic info card
        PlanetInfoCard(title = stringResource(R.string.detail_basic_info)) {
            PlanetInfoRow(label = stringResource(R.string.label_name), value = planet.name)
            PlanetInfoRow(
                label = stringResource(R.string.label_status),
                value = if (planet.isDestroyed) {
                    stringResource(R.string.label_status_destroyed)
                } else {
                    stringResource(R.string.label_status_active)
                },
                valueColor = if (planet.isDestroyed) Color.Red else Color(0xFF2E7D32)
            )
        }

        // Description
        if (planet.description.isNotEmpty()) {
            PlanetInfoCard(title = stringResource(R.string.detail_description)) {
                Text(
                    text = planet.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun PlanetInfoCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DragonBallBlue
            )
            content()
        }
    }
}

@Composable
private fun PlanetInfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
private fun FullscreenImageViewer(imageUrl: String, onDismiss: () -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onDismiss() },
                    onDoubleTap = {
                        scale = if (scale > 1f) 1f else 2.5f
                        offset = Offset.Zero
                    }
                )
            }
    ) {
        IconButton(
            onClick = onDismiss,
            modifier =
            Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.action_close),
                tint = Color.White
            )
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(R.string.cd_fullscreen_image),
            modifier =
            Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 5f)
                        offset =
                            if (scale > 1f) {
                                Offset(
                                    x = offset.x + pan.x,
                                    y = offset.y + pan.y
                                )
                            } else {
                                Offset.Zero
                            }
                    }
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit
        )
    }
}

// Preview helpers
private val previewPlanet =
    Planet(
        id = 1,
        name = "Earth",
        isDestroyed = false,
        description = "Home planet of humans and the Z Fighters. It is the main setting for most of the Dragon Ball series.",
        image = "https://dragonball-api.com/planets/earth.webp"
    )

private val previewDestroyedPlanet =
    Planet(
        id = 2,
        name = "Planet Vegeta",
        isDestroyed = true,
        description = "The original homeworld of the Saiyan race, destroyed by Frieza.",
        image = ""
    )

@Preview(showBackground = true)
@Composable
private fun PlanetDetailContentPreview() {
    AppTheme {
        PlanetDetailContent(
            planet = previewPlanet,
            onImageClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Planet Detail - Destroyed")
@Composable
private fun PlanetDetailDestroyedPreview() {
    AppTheme {
        PlanetDetailContent(
            planet = previewDestroyedPlanet,
            onImageClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Planet Info Card")
@Composable
private fun PlanetInfoCardPreview() {
    AppTheme {
        PlanetInfoCard(title = "Basic Information") {
            PlanetInfoRow(label = "Name", value = "Earth")
            PlanetInfoRow(label = "Status", value = "Active", valueColor = Color(0xFF2E7D32))
        }
    }
}
