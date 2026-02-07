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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kdragonball.android.R
import com.example.kdragonball.android.ui.component.ErrorScreen
import com.example.kdragonball.android.ui.theme.AppTheme
import com.example.kdragonball.android.ui.theme.DragonBallBlue
import com.example.kdragonball.android.ui.theme.DragonBallOrange
import com.example.kdragonball.android.ui.theme.DragonBallYellow
import com.example.kdragonball.shared.feature.character.domain.model.Character
import com.example.kdragonball.shared.feature.character.domain.model.Transformation
import com.example.kdragonball.shared.feature.character.presentation.viewmodel.CharacterDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: Int,
    onNavigateBack: () -> Unit,
    viewModel: CharacterDetailViewModel =
        koinViewModel(
            key = "character_$characterId"
        ) { parametersOf(characterId) }
) {
    val uiState by viewModel.uiState.collectAsState()
    var fullscreenImageUrl by remember { mutableStateOf<String?>(null) }

    // Handle back press when fullscreen image is shown
    BackHandler(enabled = fullscreenImageUrl != null) {
        fullscreenImageUrl = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.character?.name
                                ?: stringResource(R.string.detail_character),
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
                    uiState.character != null -> {
                        CharacterDetailContent(
                            character = uiState.character!!,
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

        // Fullscreen image overlay
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
private fun CharacterDetailContent(
    character: Character,
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
        // Character image - clickable for fullscreen
        AsyncImage(
            model = character.image,
            contentDescription = character.name,
            modifier =
            Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { onImageClick(character.image) },
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )

        // Basic info card
        InfoCard(title = stringResource(R.string.detail_basic_info)) {
            InfoRow(label = stringResource(R.string.label_name), value = character.name)
            InfoRow(label = stringResource(R.string.label_race), value = character.race)
            InfoRow(label = stringResource(R.string.label_gender), value = character.gender)
            if (character.affiliation.isNotEmpty()) {
                InfoRow(label = stringResource(R.string.label_affiliation), value = character.affiliation)
            }
            character.originPlanet?.let { planet ->
                InfoRow(label = stringResource(R.string.label_origin_planet), value = planet.name)
            }
        }

        // Power stats card - with lightning icons like iOS
        InfoCard(title = stringResource(R.string.detail_power_stats)) {
            PowerStatRow(label = stringResource(R.string.label_ki), value = character.ki)
            PowerStatRow(label = stringResource(R.string.label_max_ki), value = character.maxKi)
        }

        // Description
        if (character.description.isNotEmpty()) {
            InfoCard(title = stringResource(R.string.detail_description)) {
                Text(
                    text = character.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Transformations
        if (character.transformations.isNotEmpty()) {
            InfoCard(title = stringResource(R.string.detail_transformations)) {
                LazyRow(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(character.transformations) { transformation ->
                        TransformationCard(
                            transformation = transformation,
                            onImageClick = onImageClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
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
                color = DragonBallOrange
            )
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
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
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PowerStatRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bolt),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = DragonBallYellow
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = DragonBallOrange
        )
    }
}

@Composable
private fun TransformationCard(
    transformation: Transformation,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors =
        CardDefaults.cardColors(
            containerColor = DragonBallBlue.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = transformation.image,
                contentDescription = transformation.name,
                modifier =
                Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onImageClick(transformation.image) },
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )
            Text(
                text = transformation.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            // Ki with lightning icon like iOS
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bolt),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = DragonBallYellow
                )
                Text(
                    text = transformation.ki,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
        // Close button
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

        // Zoomable image
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
private val previewCharacter =
    Character(
        id = 1,
        name = "Goku",
        ki = "60,000,000",
        maxKi = "90 Septillion",
        race = "Saiyan",
        gender = "Male",
        description = "El protagonista de la serie, conocido por su gran poder y su personalidad amigable.",
        image = "https://dragonball-api.com/characters/goku.webp",
        affiliation = "Z Fighter",
        transformations =
        listOf(
            Transformation(
                id = 1,
                name = "Super Saiyan",
                image = "https://dragonball-api.com/transformations/goku_ssj.webp",
                ki = "3 Billion"
            ),
            Transformation(
                id = 2,
                name = "Super Saiyan 2",
                image = "https://dragonball-api.com/transformations/goku_ssj2.webp",
                ki = "6 Billion"
            )
        )
    )

@Preview(showBackground = true)
@Composable
private fun CharacterDetailContentPreview() {
    AppTheme {
        CharacterDetailContent(
            character = previewCharacter,
            onImageClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Info Card")
@Composable
private fun InfoCardPreview() {
    AppTheme {
        InfoCard(title = "Basic Information") {
            InfoRow(label = "Name", value = "Goku")
            InfoRow(label = "Race", value = "Saiyan")
        }
    }
}

@Preview(showBackground = true, name = "Power Stat Row")
@Composable
private fun PowerStatRowPreview() {
    AppTheme {
        PowerStatRow(label = "Ki", value = "60,000,000")
    }
}
