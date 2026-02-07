package com.example.kdragonball.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class CharacterListScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToCharacterList() {
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun characterListScreen_displaysTopAppBar() {
        navigateToCharacterList()

        composeTestRule.onNodeWithText("Dragon Ball Characters").assertIsDisplayed()
    }

    @Test
    fun characterListScreen_displaysSearchBar() {
        navigateToCharacterList()

        composeTestRule.onNodeWithText("Search characters...").assertIsDisplayed()
    }

    @Test
    fun characterListScreen_hasBackNavigation() {
        navigateToCharacterList()

        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun characterListScreen_searchBarAcceptsInput() {
        navigateToCharacterList()

        composeTestRule.onNodeWithText("Search characters...").performTextInput("Goku")
        composeTestRule.waitForIdle()

        // The search field should now contain "Goku"
        composeTestRule.onNodeWithText("Goku").assertIsDisplayed()
    }

    @Test
    fun characterListScreen_clearSearchShowsClearButton() {
        navigateToCharacterList()

        // Type in search field
        composeTestRule.onNodeWithText("Search characters...").performTextInput("Test")
        composeTestRule.waitForIdle()

        // Clear button should be visible
        composeTestRule.onNodeWithContentDescription("Clear").assertIsDisplayed()
    }

    @Test
    fun characterListScreen_clearButtonClearsSearch() {
        navigateToCharacterList()

        // Type in search field
        composeTestRule.onNodeWithText("Search characters...").performTextInput("Test")
        composeTestRule.waitForIdle()

        // Click clear button
        composeTestRule.onNodeWithContentDescription("Clear").performClick()
        composeTestRule.waitForIdle()

        // Search placeholder should be visible again
        composeTestRule.onNodeWithText("Search characters...").assertIsDisplayed()
    }

    @Test
    fun characterListScreen_backButtonReturnsTosDashboard() {
        navigateToCharacterList()

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Welcome to the Dragon Ball Universe").assertIsDisplayed()
    }
}
