package com.sinzunza.expensetracker7.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sinzunza.expensetracker7.data.ExpenseWithCategory
import com.sinzunza.expensetracker7.ui.CategoryListUiState
import com.sinzunza.expensetracker7.ui.CategoryViewModel
import com.sinzunza.expensetracker7.ui.EditorUiState
import com.sinzunza.expensetracker7.ui.ExpenseViewModel
import com.sinzunza.expensetracker7.ui.screens.CategoriesScreen
import com.sinzunza.expensetracker7.ui.screens.DashboardScreen
import com.sinzunza.expensetracker7.ui.screens.ExpenseFormScreen
import com.sinzunza.expensetracker7.ui.screens.ExpenseListScreen
import com.sinzunza.expensetracker7.ui.screens.SettingsScreen
import kotlinx.coroutines.flow.collect

private sealed interface ExpenseLoadState {
    data object Loading : ExpenseLoadState
    data object NotFound : ExpenseLoadState
    data class Ready(val expense: ExpenseWithCategory) : ExpenseLoadState
}

@Composable
fun ExpenseNavHost(
    expenseViewModel: ExpenseViewModel,
    categoryViewModel: CategoryViewModel,
) {
    val navController = rememberNavController()

    val listState by expenseViewModel.uiState.collectAsStateWithLifecycle()
    val editorState by expenseViewModel.editorState.collectAsStateWithLifecycle()
    val categoryListState by categoryViewModel.categoriesState.collectAsStateWithLifecycle()
    val categoryActionState by categoryViewModel.actionState.collectAsStateWithLifecycle()
    val categories = (categoryListState as? CategoryListUiState.Content)
        ?.categories
        .orEmpty()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showNavigationBar = TopLevelDestination.entries.any { destination ->
        destination.route == currentRoute
    }

    val navigateToTopLevel: (TopLevelDestination) -> Unit = { destination ->
        navController.navigate(destination.route) {
            popUpTo(
                navController.graph
                    .findStartDestination()
                    .id
            ) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        expenseViewModel.refreshMonth()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showNavigationBar) {
                ExpenseNavigationBar(
                    currentRoute = currentRoute,
                    onDestinationSelected = navigateToTopLevel,
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ExpenseRoutes.EXPENSES,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(ExpenseRoutes.EXPENSES) {
                ExpenseListScreen(
                    state = listState,
                    onAdd = {
                        expenseViewModel.resetEditorState()
                        navController.navigate(ExpenseRoutes.NEW_EXPENSE)
                    },
                    onExpenseClick = { id ->
                        expenseViewModel.resetEditorState()
                        navController.navigate(
                            ExpenseRoutes.editExpense(id)
                        )
                    },
                )
            }

            composable(ExpenseRoutes.DASHBOARD) {
                DashboardScreen(state = listState)
            }

            composable(ExpenseRoutes.CATEGORIES) {
                CategoriesScreen(
                    listState = categoryListState,
                    actionState = categoryActionState,
                    onCreateCategory = categoryViewModel::createCategory,
                    onResetActionState = categoryViewModel::resetActionState,
                )
            }

            composable(ExpenseRoutes.SETTINGS) {
                SettingsScreen()
            }

            composable(ExpenseRoutes.NEW_EXPENSE) {
                LaunchedEffect(editorState) {
                    if (editorState == EditorUiState.Saved) {
                        expenseViewModel.resetEditorState()
                        navController.popBackStack()
                    }
                }

                ExpenseFormScreen(
                    existing = null,
                    categories = categories,
                    isEditing = false,
                    isSaving = editorState == EditorUiState.Saving,
                    errorMessage = (
                            editorState as? EditorUiState.Error
                            )?.message,
                    onBack = {
                        navController.popBackStack()
                    },
                    onManageCategories = {
                        navigateToTopLevel(TopLevelDestination.CATEGORIES)
                    },
                    onSave = expenseViewModel::save,
                    onDelete = {},
                )
            }

            composable(
                route = ExpenseRoutes.EDIT_EXPENSE,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.LongType
                    },
                ),
            ) { entry ->
                val id = entry.arguments?.getLong("id")

                LaunchedEffect(editorState) {
                    if (editorState == EditorUiState.Saved) {
                        expenseViewModel.resetEditorState()
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
                        expenseViewModel.observeExpense(id).collect { expense ->
                            value = expense
                                ?.let(ExpenseLoadState::Ready)
                                ?: ExpenseLoadState.NotFound
                        }
                    }
                }

                when (val state = loadState) {
                    ExpenseLoadState.Loading -> {
                        Text("Loading…")
                    }

                    ExpenseLoadState.NotFound -> {
                        TextButton(
                            onClick = {
                                navController.popBackStack()
                            },
                        ) {
                            Text("Expense not found · go back")
                        }
                    }

                    is ExpenseLoadState.Ready -> {
                        ExpenseFormScreen(
                            existing = state.expense,
                            categories = categories,
                            isEditing = true,
                            isSaving =
                                editorState == EditorUiState.Saving,
                            errorMessage = (
                                    editorState as? EditorUiState.Error
                                    )?.message,
                            onBack = {
                                navController.popBackStack()
                            },
                            onManageCategories = {
                                navigateToTopLevel(TopLevelDestination.CATEGORIES)
                            },
                            onSave = expenseViewModel::save,
                            onDelete = expenseViewModel::delete,
                        )
                    }
                }
            }
        }
    }
}
