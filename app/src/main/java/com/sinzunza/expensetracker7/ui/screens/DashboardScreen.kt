package com.sinzunza.expensetracker7.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sinzunza.expensetracker7.ui.ExpenseListUiState
import com.sinzunza.expensetracker7.ui.navigation.ExpenseTopAppBar
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dashboardCurrencyFormatter =
    NumberFormat.getCurrencyInstance(Locale.US)
private val dashboardPercentFormatter =
    NumberFormat.getPercentInstance(Locale.US)
private val monthFormatter =
    DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)

@Composable
fun DashboardScreen(state: ExpenseListUiState) {
    Scaffold(
        topBar = {
            ExpenseTopAppBar(title = "Dashboard")
        },
    ) { innerPadding ->
        when (state) {
            ExpenseListUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is ExpenseListUiState.Error -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.message)
            }

            is ExpenseListUiState.Content -> DashboardContent(
                state = state,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun DashboardContent(
    state: ExpenseListUiState.Content,
    modifier: Modifier = Modifier,
) {
    val summary = buildDashboardSummary(state.expenses)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            TotalCard(
                totalCents = summary.totalCents,
                month = state.month.format(monthFormatter),
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricCard(
                    label = "Expenses",
                    value = "${summary.expenseCount} expenses",
                    modifier = Modifier.weight(1f),
                )
                MetricCard(
                    label = "Average",
                    value = summary.averageCents.asDashboardCurrency(),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricCard(
                    label = "Largest expense",
                    value = summary.largestExpenseCents.asDashboardCurrency(),
                    modifier = Modifier.weight(1f),
                )
                MetricCard(
                    label = "Top category",
                    value = summary.topCategoryName ?: "—",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            Text(
                text = "Spending by category",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        if (summary.categories.isEmpty()) {
            item {
                Text(
                    text = "No expenses to analyze this month.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(
                count = summary.categories.size,
                key = { index -> summary.categories[index].categoryName },
            ) { index ->
                CategoryBar(summary.categories[index])
            }
        }
    }
}

@Composable
private fun TotalCard(totalCents: Long, month: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "This month’s total",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = totalCents.asDashboardCurrency(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = month,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun CategoryBar(spending: CategorySpending) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = spending.categoryName,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "${dashboardPercentFormatter.format(spending.share)} · " +
                    spending.totalCents.asDashboardCurrency(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(spending.share.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

private fun Long.asDashboardCurrency(): String =
    dashboardCurrencyFormatter.format(this / 100.0)
