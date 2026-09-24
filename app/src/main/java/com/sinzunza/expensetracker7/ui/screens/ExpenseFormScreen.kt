package com.sinzunza.expensetracker7.ui.screens


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sinzunza.expensetracker7.data.Expense
import java.math.RoundingMode
import java.time.Instant

private fun parseCents(input: String): Long? = runCatching {
    input.trim()
        .replace(',', '.')
        .toBigDecimal()
        .movePointRight(2)
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()
}.getOrNull()?.takeIf { it > 0 }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseFormScreen(
    existing: Expense?,
    isEditing: Boolean,
    isSaving: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onSave: (Expense) -> Unit,
    onDelete: (Expense) -> Unit,
) {
    var amount by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.let { (it.amountCents / 100.0).toString() } ?: "")
    }
    var category by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.category ?: "")
    }
    var note by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.note ?: "")
    }
    var attemptedSave by rememberSaveable { mutableStateOf(false) }
    val cents = parseCents(amount)
    val amountError = attemptedSave && cents == null
    val categoryError = attemptedSave && category.isBlank()

    BackHandler(enabled = isSaving) { /* Wait for the write to finish. */ }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit expense" else "New expense") },
                navigationIcon = {
                    TextButton(onClick = onBack, enabled = !isSaving) { Text("Back") }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                prefix = { Text("$") },
                isError = amountError,
                supportingText = {
                    if (amountError) Text("Enter an amount greater than zero")
                },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                isError = categoryError,
                supportingText = {
                    if (categoryError) Text("Category is required")
                },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Optional note") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    attemptedSave = true
                    if (cents != null && category.isNotBlank()) {
                        onSave(
                            Expense(
                                id = existing?.id ?: 0,
                                amountCents = cents,
                                category = category.trim(),
                                note = note.trim(),
                                occurredAt = existing?.occurredAt ?: Instant.now(),
                            ),
                        )
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) { Text(if (isSaving) "Saving…" else "Save") }

            errorMessage?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier.semantics {
                        liveRegion = LiveRegionMode.Polite
                    },
                )
            }

            if (existing != null) {
                TextButton(
                    onClick = { onDelete(existing) },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Delete expense") }
            }
        }
    }
}