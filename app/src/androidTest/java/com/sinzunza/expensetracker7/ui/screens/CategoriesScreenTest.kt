package com.sinzunza.expensetracker7.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.sinzunza.expensetracker7.ui.CategoryActionState
import com.sinzunza.expensetracker7.ui.CategoryListUiState
import com.sinzunza.expensetracker7.ui.theme.ExpenseTracker_7Theme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CategoriesScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun createDialog_submitsEnteredCategoryName() {
        var submittedName: String? = null

        composeRule.setContent {
            ExpenseTracker_7Theme(dynamicColor = false) {
                CategoriesScreen(
                    listState = CategoryListUiState.Content(emptyList()),
                    actionState = CategoryActionState.Idle,
                    onCreateCategory = { submittedName = it },
                    onResetActionState = {},
                )
            }
        }

        composeRule.onNodeWithText("+").performClick()
        composeRule.onNodeWithTag("categoryNameInput").performTextInput("Food")
        composeRule.onNodeWithText("Create").performClick()

        composeRule.runOnIdle {
            assertEquals("Food", submittedName)
        }
    }
}
