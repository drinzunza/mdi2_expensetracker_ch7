package com.sinzunza.expensetracker7.ui.screens

import com.sinzunza.expensetracker7.data.Expense
import com.sinzunza.expensetracker7.data.ExpenseWithCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class DashboardSummaryTest {

    @Test
    fun buildDashboardSummary_calculatesMetricsAndGroupsCategories() {
        val expenses = listOf(
            expense(amountCents = 3_000, categoryId = 1, categoryName = "Food"),
            expense(amountCents = 1_000, categoryId = 1, categoryName = "Food"),
            expense(amountCents = 2_000, categoryId = 2, categoryName = "Travel"),
        )

        val summary = buildDashboardSummary(expenses)

        assertEquals(6_000, summary.totalCents)
        assertEquals(3, summary.expenseCount)
        assertEquals(2_000, summary.averageCents)
        assertEquals(3_000, summary.largestExpenseCents)
        assertEquals("Food", summary.topCategoryName)
        assertEquals(
            listOf(
                CategorySpending("Food", 4_000, 2f / 3f),
                CategorySpending("Travel", 2_000, 1f / 3f),
            ),
            summary.categories,
        )
    }

    @Test
    fun buildDashboardSummary_handlesMonthWithoutExpenses() {
        val summary = buildDashboardSummary(emptyList())

        assertEquals(0, summary.totalCents)
        assertEquals(0, summary.expenseCount)
        assertEquals(0, summary.averageCents)
        assertEquals(0, summary.largestExpenseCents)
        assertNull(summary.topCategoryName)
        assertEquals(emptyList<CategorySpending>(), summary.categories)
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
