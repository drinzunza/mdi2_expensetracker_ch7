package com.sinzunza.expensetracker7

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinzunza.expensetracker7.data.CategoryRepository
import com.sinzunza.expensetracker7.data.ExpenseRepository
import com.sinzunza.expensetracker7.ui.CategoryViewModel
import com.sinzunza.expensetracker7.ui.ExpenseViewModel
import com.sinzunza.expensetracker7.ui.navigation.ExpenseNavHost
import com.sinzunza.expensetracker7.ui.theme.ExpenseTracker_7Theme

@Composable
fun ExpenseTrackerApp(
    expenseRepository: ExpenseRepository,
    categoryRepository: CategoryRepository,
) {
    val expenseViewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModel.factory(expenseRepository),
    )
    val categoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModel.factory(categoryRepository),
    )

    ExpenseTracker_7Theme {
        ExpenseNavHost(
            expenseViewModel = expenseViewModel,
            categoryViewModel = categoryViewModel,
        )
    }
}
