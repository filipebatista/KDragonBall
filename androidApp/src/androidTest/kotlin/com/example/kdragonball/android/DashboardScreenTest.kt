package com.example.kdragonball.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.kdragonball.android.ui.screen.DashboardScreen
import com.example.kdragonball.android.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test

class DashboardScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dashboardScreen_displaysWelcomeHeader() {
        composeTestRule.setContent {
            AppTheme {
                DashboardScreen(
                    onNavigateToCharacters = {},
                    onNavigateToPlanets = {},
                    onNavigateToTransformations = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Welcome to the Dragon Ball Universe")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_displaysAllCategoryCards() {
        composeTestRule.setContent {
            AppTheme {
                DashboardScreen(
                    onNavigateToCharacters = {},
                    onNavigateToPlanets = {},
                    onNavigateToTransformations = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Characters").assertIsDisplayed()
        composeTestRule.onNodeWithText("Planets").assertIsDisplayed()
        composeTestRule.onNodeWithText("Transformations").assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_displaysCardDescriptions() {
        composeTestRule.setContent {
            AppTheme {
                DashboardScreen(
                    onNavigateToCharacters = {},
                    onNavigateToPlanets = {},
                    onNavigateToTransformations = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Browse all Dragon Ball characters")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Explore the Dragon Ball universe")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Discover powerful transformations")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_clickCharactersCard_triggersNavigation() {
        var navigatedToCharacters = false

        composeTestRule.setContent {
            AppTheme {
                DashboardScreen(
                    onNavigateToCharacters = { navigatedToCharacters = true },
                    onNavigateToPlanets = {},
                    onNavigateToTransformations = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Characters").performClick()

        assert(navigatedToCharacters) { "Expected navigation to Characters screen" }
    }

    @Test
    fun dashboardScreen_clickPlanetsCard_triggersNavigation() {
        var navigatedToPlanets = false

        composeTestRule.setContent {
            AppTheme {
                DashboardScreen(
                    onNavigateToCharacters = {},
                    onNavigateToPlanets = { navigatedToPlanets = true },
                    onNavigateToTransformations = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Planets").performClick()

        assert(navigatedToPlanets) { "Expected navigation to Planets screen" }
    }

    @Test
    fun dashboardScreen_clickTransformationsCard_triggersNavigation() {
        var navigatedToTransformations = false

        composeTestRule.setContent {
            AppTheme {
                DashboardScreen(
                    onNavigateToCharacters = {},
                    onNavigateToPlanets = {},
                    onNavigateToTransformations = { navigatedToTransformations = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Transformations").performClick()

        assert(navigatedToTransformations) { "Expected navigation to Transformations screen" }
    }

    @Test
    fun dashboardScreen_displaysTopAppBar() {
        composeTestRule.setContent {
            AppTheme {
                DashboardScreen(
                    onNavigateToCharacters = {},
                    onNavigateToPlanets = {},
                    onNavigateToTransformations = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Dragon Ball").assertIsDisplayed()
    }
}
