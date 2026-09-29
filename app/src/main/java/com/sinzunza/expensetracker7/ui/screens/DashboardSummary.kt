package com.sinzunza.expensetracker7.ui.screens

import com.sinzunza.expensetracker7.data.ExpenseWithCategory

data class CategorySpending(
    val categoryName: String,
    val totalCents: Long,
    val share: Float,
)

data class DashboardSummary(
    val totalCents: Long,
    val expenseCount: Int,
    val averageCents: Long,
    val largestExpenseCents: Long,
    val topCategoryName: String?,
    val categories: List<CategorySpending>,
)

fun buildDashboardSummary(
    expenses: List<ExpenseWithCategory>,
): DashboardSummary {
    val totalCents = expenses.sumOf { it.expense.amountCents }
    val categories = expenses
        .groupBy(ExpenseWithCategory::categoryName)
        .map { (categoryName, categoryExpenses) ->
            val categoryTotal = categoryExpenses.sumOf {
                it.expense.amountCents
            }
            CategorySpending(
                categoryName = categoryName,
                totalCents = categoryTotal,
                share = if (totalCents == 0L) {
                    0f
                } else {
                    categoryTotal.toFloat() / totalCents
                },
            )
        }
        .sortedWith(
            compareByDescending<CategorySpending> { it.totalCents }
                .thenBy { it.categoryName },
        )

    return DashboardSummary(
        totalCents = totalCents,
        expenseCount = expenses.size,
        averageCents = if (expenses.isEmpty()) {
            0
        } else {
            totalCents / expenses.size
        },
        largestExpenseCents = expenses.maxOfOrNull {
            it.expense.amountCents
        } ?: 0,
        topCategoryName = categories.firstOrNull()?.categoryName,
        categories = categories,
    )
}
