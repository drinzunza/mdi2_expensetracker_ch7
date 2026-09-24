package com.sinzunza.expensetracker7.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sinzunza.expensetracker7.data.Expense
import com.sinzunza.expensetracker7.ui.ExpenseListUiState
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)

private fun Long.asCurrency(): String =
    currencyFormatter.format(this / 100.0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    state: ExpenseListUiState,
    onAdd: () -> Unit,
    onExpenseClick: (Long) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("My expenses") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Text("+") }
        },
    ) { innerPadding ->
        when (state) {
            ExpenseListUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is ExpenseListUiState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { Text(state.message) }

            is ExpenseListUiState.Content -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
            ) {
                Text("This month’s total", fontWeight = FontWeight.Medium)
                Text(
                    text = state.totalCents.asCurrency(),
                    fontWeight = FontWeight.Bold,
                )

                if (state.expenses.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("There are no expenses this month yet")
                        TextButton(onClick = onAdd) { Text("Add the first one") }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.expenses, key = { it.id }) { expense ->
                            ExpenseRow(expense, onClick = { onExpenseClick(expense.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseRow(expense: Expense, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
    ) {
        Column {
            Text(expense.category, fontWeight = FontWeight.SemiBold)
            val date = expense.occurredAt
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            Text(expense.note.ifBlank { date.format(dateFormatter) })
        }
        Spacer(Modifier.weight(1f))
        Text(expense.amountCents.asCurrency())
    }
}