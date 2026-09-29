package com.sinzunza.expensetracker7.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExpenseDatabaseTest {

    private lateinit var database: ExpenseDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ExpenseDatabase::class.java,
        ).build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun expenseQuery_joinsCategoryName() = runBlocking {
        val categoryId = database.categoryDao().insert(
            Category(name = "Food")
        )
        val expenseId = database.expenseDao().upsert(
            Expense(
                amountCents = 1_250,
                categoryId = categoryId,
                note = "Lunch",
            )
        )

        val result = database.expenseDao().observeById(expenseId).first()

        assertNotNull(result)
        assertEquals(categoryId, result?.expense?.categoryId)
        assertEquals("Food", result?.categoryName)
    }
}
