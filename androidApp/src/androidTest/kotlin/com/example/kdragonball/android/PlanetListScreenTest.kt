package com.example.kdragonball.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class PlanetListScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToPlanetList() {
        composeTestRule.onNodeWithText("Planets").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun planetListScreen_displaysTopAppBar() {
        navigateToPlanetList()

        composeTestRule.onNodeWithText("Dragon Ball Planets").assertIsDisplayed()
    }

    @Test
    fun planetListScreen_displaysSearchBar() {
        navigateToPlanetList()

        composeTestRule.onNodeWithText("Search planets...").assertIsDisplayed()
    }

    @Test
    fun planetListScreen_hasBackNavigation() {
        navigateToPlanetList()

        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun planetListScreen_searchBarAcceptsInput() {
        navigateToPlanetList()

        composeTestRule.onNodeWithText("Search planets...").performTextInput("Earth")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Earth").assertIsDisplayed()
    }

    @Test
    fun planetListScreen_backButtonReturnsToDashboard() {
        navigateToPlanetList()

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Welcome to the Dragon Ball Universe").assertIsDisplayed()
    }
}
