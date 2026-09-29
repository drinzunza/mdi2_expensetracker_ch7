package com.sinzunza.expensetracker7.data

import kotlinx.coroutines.flow.Flow

sealed interface CreateCategoryResult {

    data class Success(
        val categoryId: Long,
    ) : CreateCategoryResult

    data object BlankName : CreateCategoryResult

    data object AlreadyExists : CreateCategoryResult
}

class CategoryRepository(
    private val categoryDao: CategoryDao,
) {
    fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll()

    suspend fun createCategory(
        name: String,
    ): CreateCategoryResult {
        val normalizedName = name.trim()

        if (normalizedName.isBlank()) {
            return CreateCategoryResult.BlankName
        }

        val categoryId = categoryDao.insert(
            Category(name = normalizedName)
        )

        return if (categoryId == -1L) {
            CreateCategoryResult.AlreadyExists
        } else {
            CreateCategoryResult.Success(
                categoryId = categoryId,
            )
        }
    }
}