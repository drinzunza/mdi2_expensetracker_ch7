package com.sinzunza.expensetracker7.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.sinzunza.expensetracker7.data.Expense
import com.sinzunza.expensetracker7.data.ExpenseWithCategory
import com.sinzunza.expensetracker7.ui.ExpenseListUiState
import com.sinzunza.expensetracker7.ui.theme.ExpenseTracker_7Theme
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.YearMonth

class DashboardScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun dashboard_displaysMonthlyMetricsAndCategoryBreakdown() {
        val state = ExpenseListUiState.Content(
            expenses = listOf(
                expense(4_000, 1, "Food"),
                expense(2_000, 2, "Travel"),
            ),
            totalCents = 6_000,
            month = YearMonth.of(2026, 9),
        )

        composeRule.setContent {
            ExpenseTracker_7Theme(dynamicColor = false) {
                DashboardScreen(state = state)
            }
        }

        composeRule.onNodeWithText("This month’s total").assertIsDisplayed()
        composeRule.onNodeWithText("\$60.00").assertIsDisplayed()
        composeRule.onNodeWithText("2 expenses").assertIsDisplayed()
        composeRule.onNodeWithText("Top category").assertIsDisplayed()
        composeRule.onAllNodesWithText("Food").assertCountEquals(2)
        composeRule.onNodeWithText("67%", substring = true).assertIsDisplayed()
    }

    private fun expense(
        amountCents: Long,
        categoryId: Long,
        categoryName: String,
    ) = ExpenseWithCategory(
        expense = Expense(
            amountCents = amountCents,
            categoryId = categoryId,
            occurredAt = Instant.parse("2026-09-15T12:00:00Z"),
        ),
        categoryName = categoryName,
    )
}
