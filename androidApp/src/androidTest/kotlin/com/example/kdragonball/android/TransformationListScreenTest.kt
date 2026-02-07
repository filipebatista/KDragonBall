package com.example.kdragonball.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class TransformationListScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToTransformationList() {
        composeTestRule.onNodeWithText("Transformations").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun transformationListScreen_displaysTopAppBar() {
        navigateToTransformationList()

        composeTestRule.onNodeWithText("Transformations").assertIsDisplayed()
    }

    @Test
    fun transformationListScreen_displaysSearchBar() {
        navigateToTransformationList()

        composeTestRule.onNodeWithText("Search transformations...").assertIsDisplayed()
    }

    @Test
    fun transformationListScreen_hasBackNavigation() {
        navigateToTransformationList()

        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun transformationListScreen_searchBarAcceptsInput() {
        navigateToTransformationList()

        composeTestRule.onNodeWithText("Search transformations...").performTextInput("Super")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Super").assertIsDisplayed()
    }

    @Test
    fun transformationListScreen_backButtonReturnsToDashboard() {
        navigateToTransformationList()

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Welcome to the Dragon Ball Universe").assertIsDisplayed()
    }
}
