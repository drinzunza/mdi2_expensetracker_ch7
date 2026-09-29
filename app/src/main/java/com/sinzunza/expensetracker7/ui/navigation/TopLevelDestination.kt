package com.sinzunza.expensetracker7.ui.navigation

import androidx.annotation.DrawableRes
import com.sinzunza.expensetracker7.R

object ExpenseRoutes {
    const val EXPENSES = "expenses"
    const val DASHBOARD = "dashboard"
    const val CATEGORIES = "categories"
    const val SETTINGS = "settings"

    const val NEW_EXPENSE = "expense/new"
    const val EDIT_EXPENSE = "expense/{id}"

    fun editExpense(id: Long): String = "expense/$id"
}

enum class TopLevelDestination(
    val route: String,
    val label: String,
    @param:DrawableRes val iconRes: Int,
) {
    EXPENSES(
        route = ExpenseRoutes.EXPENSES,
        label = "Expenses",
        iconRes = R.drawable.ic_expenses,
    ),
    DASHBOARD(
        route = ExpenseRoutes.DASHBOARD,
        label = "Dashboard",
        iconRes = R.drawable.ic_dashboard,
    ),
    CATEGORIES(
        route = ExpenseRoutes.CATEGORIES,
        label = "Categories",
        iconRes = R.drawable.ic_categories,
    ),
    SETTINGS(
        route = ExpenseRoutes.SETTINGS,
        label = "Settings",
        iconRes = R.drawable.ic_settings,
    ),
}
