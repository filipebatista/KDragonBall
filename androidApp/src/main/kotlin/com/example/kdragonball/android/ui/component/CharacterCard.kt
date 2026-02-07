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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kdragonball.android.R
import com.example.kdragonball.android.ui.theme.AppTheme
import com.example.kdragonball.android.ui.theme.DragonBallBlue
import com.example.kdragonball.android.ui.theme.DragonBallOrange
import com.example.kdragonball.shared.feature.character.domain.model.Character

@Composable
fun CharacterCard(character: Character, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
            // Character image - aligned to top to show face
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier =
                Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            // Character info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = character.race,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Ki with lightning icon - Orange color like iOS
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bolt),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = DragonBallOrange
                    )
                    Text(
                        text = stringResource(R.string.format_ki, character.ki),
                        style = MaterialTheme.typography.labelSmall,
                        color = DragonBallOrange,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Affiliation badge - Blue for heroes, Red for villains like iOS
                if (character.affiliation.isNotEmpty()) {
                    AffiliationChip(
                        affiliation = character.affiliation,
                        isHero = character.isHero
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
private fun AffiliationChip(affiliation: String, isHero: Boolean, modifier: Modifier = Modifier) {
    val backgroundColor =
        if (isHero) {
            DragonBallBlue.copy(alpha = 0.2f)
        } else {
            Color.Red.copy(alpha = 0.2f)
        }

    val textColor =
        if (isHero) {
            DragonBallBlue
        } else {
            Color.Red
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
            text = affiliation,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Preview helpers
private val previewHeroCharacter =
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
    )

private val previewVillainCharacter =
    Character(
        id = 2,
        name = "Frieza",
        ki = "530,000,000",
        maxKi = "100 Quintillion",
        race = "Frost Demon",
        gender = "Male",
        description = "El tirano del universo.",
        image = "https://dragonball-api.com/characters/frieza.webp",
        affiliation = "Frieza Army"
    )

@Preview(showBackground = true)
@Composable
private fun CharacterCardHeroPreview() {
    AppTheme {
        CharacterCard(
            character = previewHeroCharacter,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Character Card - Villain")
@Composable
private fun CharacterCardVillainPreview() {
    AppTheme {
        CharacterCard(
            character = previewVillainCharacter,
            onClick = {}
        )
    }
}
