package com.example.kdragonball.android.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object Routes {
    const val DASHBOARD = "dashboard"
    const val CHARACTER_LIST = "character_list"
    const val CHARACTER_DETAIL = "character_detail/{characterId}"
    const val PLANET_LIST = "planet_list"
    const val PLANET_DETAIL = "planet_detail/{planetId}"
    const val TRANSFORMATION_LIST = "transformation_list"
    const val TRANSFORMATION_DETAIL = "transformation_detail/{transformationId}"

    fun characterDetail(characterId: Int) = "character_detail/$characterId"

    fun planetDetail(planetId: Int) = "planet_detail/$planetId"

    fun transformationDetail(transformationId: Int) = "transformation_detail/$transformationId"
}

@Composable
fun DragonBallApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD
    ) {
        // Dashboard
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigateToCharacters = {
                    navController.navigate(Routes.CHARACTER_LIST)
                },
                onNavigateToPlanets = {
                    navController.navigate(Routes.PLANET_LIST)
                },
                onNavigateToTransformations = {
                    navController.navigate(Routes.TRANSFORMATION_LIST)
                }
            )
        }

        // Character screens
        composable(Routes.CHARACTER_LIST) {
            CharacterListScreen(
                onNavigateToDetail = { characterId ->
                    navController.navigate(Routes.characterDetail(characterId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.CHARACTER_DETAIL,
            arguments =
            listOf(
                navArgument("characterId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("characterId") ?: return@composable
            CharacterDetailScreen(
                characterId = characterId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Planet screens
        composable(Routes.PLANET_LIST) {
            PlanetListScreen(
                onNavigateToDetail = { planetId ->
                    navController.navigate(Routes.planetDetail(planetId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.PLANET_DETAIL,
            arguments =
            listOf(
                navArgument("planetId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val planetId = backStackEntry.arguments?.getInt("planetId") ?: return@composable
            PlanetDetailScreen(
                planetId = planetId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Transformation screens
        composable(Routes.TRANSFORMATION_LIST) {
            TransformationListScreen(
                onNavigateToDetail = { transformationId ->
                    navController.navigate(Routes.transformationDetail(transformationId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.TRANSFORMATION_DETAIL,
            arguments =
            listOf(
                navArgument("transformationId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val transformationId = backStackEntry.arguments?.getInt("transformationId") ?: return@composable
            TransformationDetailScreen(
                transformationId = transformationId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
