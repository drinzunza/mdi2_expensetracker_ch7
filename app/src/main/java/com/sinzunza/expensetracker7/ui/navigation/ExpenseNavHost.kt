package com.sinzunza.expensetracker7.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sinzunza.expensetracker7.ui.ExpenseViewModel
import com.sinzunza.expensetracker7.ui.EditorUiState
import com.sinzunza.expensetracker7.ui.screens.ExpenseFormScreen
import com.sinzunza.expensetracker7.ui.screens.ExpenseListScreen
import com.sinzunza.expensetracker7.data.Expense
import kotlinx.coroutines.flow.collect

private sealed interface ExpenseLoadState {
    data object Loading : ExpenseLoadState
    data object NotFound : ExpenseLoadState
    data class Ready(val expense: Expense) : ExpenseLoadState
}

private const val EXPENSES = "expenses"
private const val NEW_EXPENSE = "expense/new"
private const val EDIT_EXPENSE = "expense/{id}"

@Composable
fun ExpenseNavHost(viewModel: ExpenseViewModel) {
    val navController = rememberNavController()
    val listState by viewModel.uiState.collectAsStateWithLifecycle()
    val editorState by viewModel.editorState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refreshMonth()
    }

    NavHost(navController = navController, startDestination = EXPENSES) {
        composable(EXPENSES) {
            ExpenseListScreen(
                state = listState,
                onAdd = {
                    viewModel.resetEditorState()
                    navController.navigate(NEW_EXPENSE)
                },
                onExpenseClick = { id ->
                    viewModel.resetEditorState()
                    navController.navigate("expense/$id")
                },
            )
        }

        composable(NEW_EXPENSE) {
            LaunchedEffect(editorState) {
                if (editorState == EditorUiState.Saved) {
                    viewModel.resetEditorState()
                    navController.popBackStack()
                }
            }
            ExpenseFormScreen(
                existing = null,
                isEditing = false,
                isSaving = editorState == EditorUiState.Saving,
                errorMessage = (editorState as? EditorUiState.Error)?.message,
                onBack = { navController.popBackStack() },
                onSave = viewModel::save,
                onDelete = {},
            )
        }

        composable(
            route = EDIT_EXPENSE,
            arguments = listOf(navArgument("id") { type = NavType.LongType }),
        ) { entry ->
            val id = entry.arguments?.getLong("id")
            LaunchedEffect(editorState) {
                if (editorState == EditorUiState.Saved) {
                    viewModel.resetEditorState()
                    navController.popBackStack()
                }
            }
            val loadState by produceState<ExpenseLoadState>(
                initialValue = ExpenseLoadState.Loading,
                key1 = id,
            ) {
                if (id == null) {
                    value = ExpenseLoadState.NotFound
                } else {
                    viewModel.observeExpense(id).collect { expense ->
                        value = expense
                            ?.let(ExpenseLoadState::Ready)
                            ?: ExpenseLoadState.NotFound
                    }
                }
            }

            when (val state = loadState) {
                ExpenseLoadState.Loading -> Text("Loading…")
                ExpenseLoadState.NotFound -> TextButton(
                    onClick = { navController.popBackStack() },
                ) { Text("Expense not found · go back") }
                is ExpenseLoadState.Ready -> ExpenseFormScreen(
                    existing = state.expense,
                    isEditing = true,
                    isSaving = editorState == EditorUiState.Saving,
                    errorMessage = (editorState as? EditorUiState.Error)?.message,
                    onBack = { navController.popBackStack() },
                    onSave = viewModel::save,
                    onDelete = viewModel::delete,
                )
            }
        }
    }
}