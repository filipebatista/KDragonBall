package com.example.kdragonball.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class NavigationTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appStartsOnDashboard() {
        composeTestRule.onNodeWithText("Dragon Ball").assertIsDisplayed()
        composeTestRule.onNodeWithText("Welcome to the Dragon Ball Universe").assertIsDisplayed()
    }

    @Test
    fun navigateToCharacterList_andBack() {
        // Start on dashboard
        composeTestRule.onNodeWithText("Characters").assertIsDisplayed()

        // Navigate to character list
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()

        // Verify we're on character list
        composeTestRule.onNodeWithText("Dragon Ball Characters").assertIsDisplayed()

        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()

        // Verify we're back on dashboard
        composeTestRule.onNodeWithText("Welcome to the Dragon Ball Universe").assertIsDisplayed()
    }

    @Test
    fun navigateToPlanetList_andBack() {
        // Navigate to planet list
        composeTestRule.onNodeWithText("Planets").performClick()
        composeTestRule.waitForIdle()

        // Verify we're on planet list
        composeTestRule.onNodeWithText("Dragon Ball Planets").assertIsDisplayed()

        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()

        // Verify we're back on dashboard
        composeTestRule.onNodeWithText("Welcome to the Dragon Ball Universe").assertIsDisplayed()
    }

    @Test
    fun navigateToTransformationList_andBack() {
        // Navigate to transformation list
        composeTestRule.onNodeWithText("Transformations").performClick()
        composeTestRule.waitForIdle()

        // Verify we're on transformation list
        composeTestRule.onNodeWithText("Transformations").assertIsDisplayed()

        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()

        // Verify we're back on dashboard
        composeTestRule.onNodeWithText("Welcome to the Dragon Ball Universe").assertIsDisplayed()
    }

    @Test
    fun navigationBetweenCategories() {
        // Navigate to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Dragon Ball Characters").assertIsDisplayed()

        // Go back
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()

        // Navigate to Planets
        composeTestRule.onNodeWithText("Planets").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Dragon Ball Planets").assertIsDisplayed()

        // Go back
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()

        // Navigate to Transformations
        composeTestRule.onNodeWithText("Transformations").performClick()
        composeTestRule.waitForIdle()

        // Go back to dashboard
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()

        // Verify back on dashboard
        composeTestRule.onNodeWithText("Welcome to the Dragon Ball Universe").assertIsDisplayed()
    }
}
