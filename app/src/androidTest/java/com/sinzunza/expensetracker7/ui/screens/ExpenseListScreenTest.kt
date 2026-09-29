package com.sinzunza.expensetracker7.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.sinzunza.expensetracker7.ui.ExpenseListUiState
import com.sinzunza.expensetracker7.ui.theme.ExpenseTracker_7Theme
import org.junit.Rule
import org.junit.Test
import java.time.YearMonth

class ExpenseListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun expensesScreen_doesNotShowDashboardSummary() {
        composeRule.setContent {
            ExpenseTracker_7Theme(dynamicColor = false) {
                ExpenseListScreen(
                    state = ExpenseListUiState.Content(
                        expenses = emptyList(),
                        totalCents = 0,
                        month = YearMonth.of(2026, 9),
                    ),
                    onAdd = {},
                    onExpenseClick = {},
                )
            }
        }

        composeRule.onNodeWithText("This month’s total").assertDoesNotExist()
    }
}
