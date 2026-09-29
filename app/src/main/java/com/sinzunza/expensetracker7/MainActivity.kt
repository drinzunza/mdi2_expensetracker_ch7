package com.sinzunza.expensetracker7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sinzunza.expensetracker7.data.CategoryRepository
import com.sinzunza.expensetracker7.data.ExpenseDatabase
import com.sinzunza.expensetracker7.data.ExpenseRepository

class MainActivity : ComponentActivity() {
    private val database by lazy {
        ExpenseDatabase.getInstance(applicationContext)
    }
    private val expenseRepository by lazy {
        ExpenseRepository(database.expenseDao())
    }
    private val categoryRepository by lazy {
        CategoryRepository(database.categoryDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerApp(
                expenseRepository = expenseRepository,
                categoryRepository = categoryRepository,
            )
        }
    }
}
