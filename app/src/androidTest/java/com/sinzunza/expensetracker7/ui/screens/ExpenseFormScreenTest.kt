package com.sinzunza.expensetracker7.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.sinzunza.expensetracker7.data.Category
import com.sinzunza.expensetracker7.data.Expense
import com.sinzunza.expensetracker7.ui.theme.ExpenseTracker_7Theme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ExpenseFormScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun selectingCategory_savesItsId() {
        var savedExpense: Expense? = null

        composeRule.setContent {
            ExpenseTracker_7Theme(dynamicColor = false) {
                ExpenseFormScreen(
                    existing = null,
                    categories = listOf(
                        Category(id = 7, name = "Food"),
                        Category(id = 8, name = "Travel"),
                    ),
                    isEditing = false,
                    isSaving = false,
                    errorMessage = null,
                    onBack = {},
                    onManageCategories = {},
                    onSave = { savedExpense = it },
                    onDelete = {},
                )
            }
        }

        composeRule.onNodeWithTag("amountInput").performTextInput("12.50")
        composeRule.onNodeWithTag("categoryDropdown").performClick()
        composeRule.onNodeWithText("Food").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("saveExpense").performClick()

        composeRule.runOnIdle {
            assertEquals(7L, savedExpense?.categoryId)
        }
    }
}
