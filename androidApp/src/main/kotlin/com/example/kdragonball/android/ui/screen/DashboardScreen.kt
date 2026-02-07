package com.example.kdragonball.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kdragonball.android.R
import com.example.kdragonball.android.ui.theme.AppTheme
import com.example.kdragonball.android.ui.theme.DragonBallBlue
import com.example.kdragonball.android.ui.theme.DragonBallOrange
import com.example.kdragonball.android.ui.theme.DragonBallPurple

enum class DashboardItem(
    val titleRes: Int,
    val descriptionRes: Int,
    val gradientColors: List<Color>
) {
    CHARACTERS(
        titleRes = R.string.dashboard_characters,
        descriptionRes = R.string.dashboard_characters_desc,
        gradientColors = listOf(DragonBallOrange, DragonBallOrange.copy(alpha = 0.7f))
    ),
    PLANETS(
        titleRes = R.string.dashboard_planets,
        descriptionRes = R.string.dashboard_planets_desc,
        gradientColors = listOf(DragonBallBlue, DragonBallBlue.copy(alpha = 0.7f))
    ),
    TRANSFORMATIONS(
        titleRes = R.string.dashboard_transformations,
        descriptionRes = R.string.dashboard_transformations_desc,
        gradientColors = listOf(DragonBallPurple, DragonBallPurple.copy(alpha = 0.7f))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToCharacters: () -> Unit,
    onNavigateToPlanets: () -> Unit,
    onNavigateToTransformations: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dashboard_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = stringResource(R.string.dashboard_welcome),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = stringResource(R.string.dashboard_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dashboard Items Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(DashboardItem.entries) { item ->
                    DashboardCard(
                        item = item,
                        onClick = {
                            when (item) {
                                DashboardItem.CHARACTERS -> onNavigateToCharacters()
                                DashboardItem.PLANETS -> onNavigateToPlanets()
                                DashboardItem.TRANSFORMATIONS -> onNavigateToTransformations()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(item: DashboardItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier =
        modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(item.gradientColors)
                )
        ) {
            Row(
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(item.titleRes),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(item.descriptionRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                // Icon
                Box(
                    modifier =
                    Modifier
                        .size(60.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (item) {
                        DashboardItem.CHARACTERS -> {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color.White
                            )
                        }
                        DashboardItem.PLANETS -> {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_planet),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color.White
                            )
                        }
                        DashboardItem.TRANSFORMATIONS -> {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_bolt),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    AppTheme {
        DashboardScreen(
            onNavigateToCharacters = {},
            onNavigateToPlanets = {},
            onNavigateToTransformations = {}
        )
    }
}

@Preview(showBackground = true, name = "Dashboard Card - Characters")
@Composable
private fun DashboardCardPreview() {
    AppTheme {
        DashboardCard(
            item = DashboardItem.CHARACTERS,
            onClick = {}
        )
    }
}
