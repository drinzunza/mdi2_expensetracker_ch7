package com.sinzunza.expensetracker7.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["categoryId"]),
    ],
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amountCents: Long,
    val categoryId: Long,
    val note: String = "",
    val occurredAt: Instant = Instant.now(),
)

data class ExpenseWithCategory(
    @Embedded
    val expense: Expense,
    val categoryName: String,
)
