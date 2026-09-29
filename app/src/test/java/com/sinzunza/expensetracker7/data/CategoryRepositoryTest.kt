package com.sinzunza.expensetracker7.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryRepositoryTest {

    @Test
    fun createCategory_trimsNameAndReturnsGeneratedId() = runBlocking {
        val dao = FakeCategoryDao(insertResult = 7L)
        val repository = CategoryRepository(dao)

        val result = repository.createCategory("  Food  ")

        assertEquals("Food", dao.lastInserted?.name)
        assertEquals(CreateCategoryResult.Success(categoryId = 7L), result)
    }

    @Test
    fun createCategory_rejectsBlankNameWithoutWriting() = runBlocking {
        val dao = FakeCategoryDao(insertResult = 1L)
        val repository = CategoryRepository(dao)

        val result = repository.createCategory("   ")

        assertEquals(CreateCategoryResult.BlankName, result)
        assertEquals(null, dao.lastInserted)
    }

    @Test
    fun createCategory_reportsDuplicateWhenRoomIgnoresInsert() = runBlocking {
        val repository = CategoryRepository(FakeCategoryDao(insertResult = -1L))

        val result = repository.createCategory("Food")

        assertTrue(result is CreateCategoryResult.AlreadyExists)
    }

    private class FakeCategoryDao(
        private val insertResult: Long,
    ) : CategoryDao {
        private val categories = MutableStateFlow<List<Category>>(emptyList())
        var lastInserted: Category? = null

        override fun observeAll(): Flow<List<Category>> = categories

        override suspend fun insert(category: Category): Long {
            lastInserted = category
            return insertResult
        }
    }
}
