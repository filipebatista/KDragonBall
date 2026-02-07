package com.example.kdragonball.android.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kdragonball.android.R
import com.example.kdragonball.android.ui.theme.AppTheme
import com.example.kdragonball.android.ui.theme.DragonBallBlue
import com.example.kdragonball.shared.feature.planet.domain.model.Planet

@Composable
fun PlanetCard(planet: Planet, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier =
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Planet image
            AsyncImage(
                model = planet.image.ifEmpty { null },
                contentDescription = planet.name,
                modifier =
                Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(DragonBallBlue.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop
            )

            // Planet info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = planet.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Status badge
                StatusChip(
                    isDestroyed = planet.isDestroyed
                )

                // Description preview
                if (planet.description.isNotEmpty()) {
                    Text(
                        text = planet.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Chevron right icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusChip(isDestroyed: Boolean, modifier: Modifier = Modifier) {
    val backgroundColor =
        if (isDestroyed) {
            Color.Red.copy(alpha = 0.2f)
        } else {
            Color.Green.copy(alpha = 0.2f)
        }

    val textColor =
        if (isDestroyed) {
            Color.Red
        } else {
            Color(0xFF2E7D32) // Dark green
        }

    val statusText = if (isDestroyed) {
        stringResource(R.string.label_status_destroyed)
    } else {
        stringResource(R.string.label_status_active)
    }

    Box(
        modifier =
        modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

// Preview helpers
private val previewActivePlanet =
    Planet(
        id = 1,
        name = "Earth",
        isDestroyed = false,
        description = "Home planet of humans and the Z Fighters.",
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
private fun PlanetCardActivePreview() {
    AppTheme {
        PlanetCard(
            planet = previewActivePlanet,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Planet Card - Destroyed")
@Composable
private fun PlanetCardDestroyedPreview() {
    AppTheme {
        PlanetCard(
            planet = previewDestroyedPlanet,
            onClick = {}
        )
    }
}
