package com.sinzunza.expensetracker7.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTopAppBar(
    title: String,
    showBackButton: Boolean = false,
    backEnabled: Boolean = true,
    onBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    val contentColor = MaterialTheme.colorScheme.onPrimary

    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            if (showBackButton) {
                TextButton(
                    onClick = onBack,
                    enabled = backEnabled,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = contentColor,
                        disabledContentColor = contentColor.copy(alpha = 0.38f),
                    ),
                ) {
                    Text("Back")
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor,
        ),
    )
}