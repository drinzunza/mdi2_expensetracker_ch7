package com.sinzunza.expensetracker7.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface ExpenseDao {

    @Query(
        """
        SELECT expenses.*, categories.name AS categoryName
        FROM expenses
        INNER JOIN categories ON categories.id = expenses.categoryId
        WHERE expenses.id = :id
        LIMIT 1
        """
    )
    fun observeById(id: Long): Flow<ExpenseWithCategory?>

    @Upsert
    suspend fun upsert(expense: Expense): Long

    @Delete
    suspend fun delete(expense: Expense)

    @Query("""
        SELECT expenses.*, categories.name AS categoryName
        FROM expenses
        INNER JOIN categories ON categories.id = expenses.categoryId
        WHERE expenses.occurredAt >= :startInclusive
          AND expenses.occurredAt < :endExclusive
        ORDER BY expenses.occurredAt DESC
    """)
    fun observeBetween(
        startInclusive: Instant,
        endExclusive: Instant,
    ): Flow<List<ExpenseWithCategory>>
}
