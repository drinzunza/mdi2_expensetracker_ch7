package com.sinzunza.expensetracker7.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.sinzunza.expensetracker7.ui.CategoryActionState
import com.sinzunza.expensetracker7.ui.CategoryListUiState
import com.sinzunza.expensetracker7.ui.navigation.ExpenseTopAppBar

@Composable
fun CategoriesScreen(
    listState: CategoryListUiState,
    actionState: CategoryActionState,
    onCreateCategory: (String) -> Unit,
    onResetActionState: () -> Unit,
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var categoryName by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(actionState) {
        if (actionState is CategoryActionState.Created) {
            showCreateDialog = false
            categoryName = ""
            onResetActionState()
        }
    }

    Scaffold(
        topBar = {
            ExpenseTopAppBar(title = "Categories")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onResetActionState()
                    categoryName = ""
                    showCreateDialog = true
                },
            ) {
                Text("+")
            }
        },
    ) { innerPadding ->
        when (listState) {
            CategoryListUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is CategoryListUiState.Error -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(listState.message)
            }

            is CategoryListUiState.Content -> {
                if (listState.categories.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "No categories yet",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text("Use + to create your first category.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(vertical = 8.dp),
                    ) {
                        items(
                            items = listState.categories,
                            key = { category -> category.id },
                        ) { category ->
                            ListItem(
                                headlineContent = { Text(category.name) },
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        val isSaving = actionState == CategoryActionState.Saving
        val errorMessage = (actionState as? CategoryActionState.Error)?.message

        AlertDialog(
            onDismissRequest = {
                if (!isSaving) {
                    showCreateDialog = false
                    onResetActionState()
                }
            },
            title = { Text("New category") },
            text = {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = {
                        categoryName = it
                        if (actionState is CategoryActionState.Error) {
                            onResetActionState()
                        }
                    },
                    label = { Text("Category name") },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = {
                        errorMessage?.let { Text(it) }
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("categoryNameInput"),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { onCreateCategory(categoryName) },
                    enabled = !isSaving,
                ) {
                    Text(if (isSaving) "Creating…" else "Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateDialog = false
                        onResetActionState()
                    },
                    enabled = !isSaving,
                ) {
                    Text("Cancel")
                }
            },
        )
    }
}
