package com.example.kdragonball.android.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.example.kdragonball.android.ui.theme.DragonBallOrange
import com.example.kdragonball.android.ui.theme.DragonBallPurple
import com.example.kdragonball.android.ui.theme.DragonBallYellow
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationDetail

@Composable
fun TransformationListCard(
    transformation: TransformationDetail,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            // Transformation image
            AsyncImage(
                model = transformation.image.ifEmpty { null },
                contentDescription = transformation.name,
                modifier =
                Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(DragonBallPurple.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            // Transformation info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = transformation.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Ki with lightning icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bolt),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = DragonBallYellow
                    )
                    Text(
                        text = stringResource(R.string.format_ki, transformation.ki),
                        style = MaterialTheme.typography.labelSmall,
                        color = DragonBallOrange,
                        fontWeight = FontWeight.Medium
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

// Preview helpers
private val previewTransformation =
    TransformationDetail(
        id = 1,
        name = "Super Saiyan",
        image = "https://dragonball-api.com/transformations/goku_ssj.webp",
        ki = "3 Billion"
    )

private val previewTransformationLongName =
    TransformationDetail(
        id = 2,
        name = "Super Saiyan God Super Saiyan (Blue)",
        image = "",
        ki = "100 Quintillion"
    )

@Preview(showBackground = true)
@Composable
private fun TransformationListCardPreview() {
    AppTheme {
        TransformationListCard(
            transformation = previewTransformation,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Transformation Card - Long Name")
@Composable
private fun TransformationListCardLongNamePreview() {
    AppTheme {
        TransformationListCard(
            transformation = previewTransformationLongName,
            onClick = {}
        )
    }
}
