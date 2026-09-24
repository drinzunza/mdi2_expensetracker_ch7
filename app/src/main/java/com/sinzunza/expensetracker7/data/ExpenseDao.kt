package com.sinzunza.expensetracker7.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<Expense?>

    @Upsert
    suspend fun upsert(expense: Expense): Long

    @Delete
    suspend fun delete(expense: Expense)

    @Query("""
        SELECT * FROM expenses
        WHERE occurredAt >= :startInclusive AND occurredAt < :endExclusive
        ORDER BY occurredAt DESC
    """)
    fun observeBetween(
        startInclusive: Instant,
        endExclusive: Instant,
    ): Flow<List<Expense>>
}