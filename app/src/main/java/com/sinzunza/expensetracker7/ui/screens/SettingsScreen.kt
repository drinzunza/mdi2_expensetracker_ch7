package com.sinzunza.expensetracker7.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sinzunza.expensetracker7.ui.navigation.ExpenseTopAppBar

@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            ExpenseTopAppBar(title = "Settings")
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("App preferences will appear here.")
        }
    }
}