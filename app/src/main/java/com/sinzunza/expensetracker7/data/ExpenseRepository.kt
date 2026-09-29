package com.sinzunza.expensetracker7.data

import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import java.time.ZoneId

class ExpenseRepository(private val dao: ExpenseDao) {
    fun observeMonth(
        month: YearMonth,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): Flow<List<ExpenseWithCategory>> {
        val start = month.atDay(1).atStartOfDay(zoneId).toInstant()
        val end = month.plusMonths(1).atDay(1).atStartOfDay(zoneId).toInstant()
        return dao.observeBetween(start, end)
    }

    fun observeExpense(id: Long): Flow<ExpenseWithCategory?> =
        dao.observeById(id)

    suspend fun save(expense: Expense): Long =
        dao.upsert(expense)

    suspend fun delete(expense: Expense) =
        dao.delete(expense)
}
