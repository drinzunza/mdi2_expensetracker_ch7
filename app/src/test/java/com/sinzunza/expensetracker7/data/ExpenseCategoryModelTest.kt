package com.sinzunza.expensetracker7.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ExpenseCategoryModelTest {

    @Test
    fun expenseWithCategory_exposesStoredCategoryIdAndDisplayName() {
        val item = ExpenseWithCategory(
            expense = Expense(
                amountCents = 1_250,
                categoryId = 7,
                note = "Lunch",
            ),
            categoryName = "Food",
        )

        assertEquals(7L, item.expense.categoryId)
        assertEquals("Food", item.categoryName)
    }
}
