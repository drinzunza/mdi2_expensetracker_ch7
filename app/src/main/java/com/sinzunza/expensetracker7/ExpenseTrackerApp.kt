package com.sinzunza.expensetracker7

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinzunza.expensetracker7.data.ExpenseRepository
import com.sinzunza.expensetracker7.ui.ExpenseViewModel
import com.sinzunza.expensetracker7.ui.navigation.ExpenseNavHost
import com.sinzunza.expensetracker7.ui.theme.ExpenseTracker_7Theme

@Composable
fun ExpenseTrackerApp(repository: ExpenseRepository) {
    val viewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModel.factory(repository),
    )

    ExpenseTracker_7Theme {
        ExpenseNavHost(viewModel)
    }
}