package com.sinzunza.expensetracker7.ui.screens


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sinzunza.expensetracker7.data.Category
import com.sinzunza.expensetracker7.data.Expense
import com.sinzunza.expensetracker7.data.ExpenseWithCategory
import java.math.RoundingMode
import java.time.Instant

import com.sinzunza.expensetracker7.ui.navigation.ExpenseTopAppBar

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
    existing: ExpenseWithCategory?,
    categories: List<Category>,
    isEditing: Boolean,
    isSaving: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onManageCategories: () -> Unit,
    onSave: (Expense) -> Unit,
    onDelete: (Expense) -> Unit,
) {
    val existingExpense = existing?.expense

    var amount by rememberSaveable(existingExpense?.id) {
        mutableStateOf(existingExpense?.let { (it.amountCents / 100.0).toString() } ?: "")
    }
    var selectedCategoryId by rememberSaveable(existingExpense?.id) {
        mutableStateOf(existingExpense?.categoryId)
    }
    var categoryMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var note by rememberSaveable(existingExpense?.id) {
        mutableStateOf(existingExpense?.note ?: "")
    }
    var attemptedSave by rememberSaveable { mutableStateOf(false) }
    val cents = parseCents(amount)
    val amountError = attemptedSave && cents == null
    val categoryError = attemptedSave && selectedCategoryId == null
    val selectedCategoryName = categories
        .firstOrNull { it.id == selectedCategoryId }
        ?.name
        ?: existing?.categoryName.orEmpty()

    BackHandler(enabled = isSaving) { /* Wait for the write to finish. */ }

    Scaffold(
        topBar = {
            ExpenseTopAppBar(
                title = if (isEditing) {
                    "Edit expense"
                } else {
                    "New expense"
                },
                showBackButton = true,
                backEnabled = !isSaving,
                onBack = onBack,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("amountInput"),
            )

            ExposedDropdownMenuBox(
                expanded = categoryMenuExpanded,
                onExpandedChange = { shouldExpand ->
                    if (categories.isNotEmpty() && !isSaving) {
                        categoryMenuExpanded = shouldExpand
                    }
                },
            ) {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    placeholder = { Text("Select a category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = categoryMenuExpanded,
                        )
                    },
                    isError = categoryError,
                    supportingText = {
                        when {
                            categories.isEmpty() -> Text("Create a category before saving")
                            categoryError -> Text("Select a category")
                        }
                    },
                    modifier = Modifier
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = categories.isNotEmpty() && !isSaving,
                        )
                        .fillMaxWidth()
                        .testTag("categoryDropdown"),
                )

                ExposedDropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = { categoryMenuExpanded = false },
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategoryId = category.id
                                categoryMenuExpanded = false
                            },
                        )
                    }
                }
            }

            if (categories.isEmpty()) {
                TextButton(
                    onClick = onManageCategories,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Manage categories")
                }
            }
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Optional note") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    attemptedSave = true
                    val categoryId = selectedCategoryId
                    if (cents != null && categoryId != null) {
                        onSave(
                            Expense(
                                id = existingExpense?.id ?: 0,
                                amountCents = cents,
                                categoryId = categoryId,
                                note = note.trim(),
                                occurredAt = existingExpense?.occurredAt ?: Instant.now(),
                            ),
                        )
                    }
                },
                enabled = !isSaving && categories.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("saveExpense"),
            ) { Text(if (isSaving) "Saving…" else "Save") }

            errorMessage?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier.semantics {
                        liveRegion = LiveRegionMode.Polite
                    },
                )
            }

            if (existingExpense != null) {
                TextButton(
                    onClick = { onDelete(existingExpense) },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Delete expense") }
            }
        }
    }
}
