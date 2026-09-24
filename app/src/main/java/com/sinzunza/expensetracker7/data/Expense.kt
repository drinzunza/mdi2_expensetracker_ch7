package com.sinzunza.expensetracker7.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName="expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amountCents: Long,
    val category: String,
    val note: String = "",
    val occurredAt: Instant = Instant.now(),
)