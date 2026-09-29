package com.sinzunza.expensetracker7.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sinzunza.expensetracker7.data.Category
import com.sinzunza.expensetracker7.data.CategoryRepository
import com.sinzunza.expensetracker7.data.CreateCategoryResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CategoryListUiState {

    data object Loading : CategoryListUiState

    data class Content(
        val categories: List<Category>,
    ) : CategoryListUiState

    data class Error(
        val message: String,
    ) : CategoryListUiState
}

sealed interface CategoryActionState {

    data object Idle : CategoryActionState

    data object Saving : CategoryActionState

    data class Created(
        val categoryId: Long,
    ) : CategoryActionState

    data class Error(
        val message: String,
    ) : CategoryActionState
}

class CategoryViewModel(
    private val repository: CategoryRepository,
) : ViewModel() {

    val categoriesState: StateFlow<CategoryListUiState> =
        repository
            .observeCategories()
            .map<List<Category>, CategoryListUiState> { categories ->
                CategoryListUiState.Content(
                    categories = categories,
                )
            }
            .catch {
                emit(
                    CategoryListUiState.Error(
                        message = "Categories could not be loaded.",
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CategoryListUiState.Loading,
            )

    private val _actionState =
        MutableStateFlow<CategoryActionState>(
            CategoryActionState.Idle
        )

    val actionState: StateFlow<CategoryActionState> =
        _actionState.asStateFlow()

    fun createCategory(name: String) {
        if (_actionState.value == CategoryActionState.Saving) {
            return
        }

        _actionState.value = CategoryActionState.Saving

        viewModelScope.launch {
            _actionState.value = try {
                when (
                    val result = repository.createCategory(name)
                ) {
                    is CreateCategoryResult.Success -> {
                        CategoryActionState.Created(
                            categoryId = result.categoryId,
                        )
                    }

                    CreateCategoryResult.BlankName -> {
                        CategoryActionState.Error(
                            message = "Enter a category name.",
                        )
                    }

                    CreateCategoryResult.AlreadyExists -> {
                        CategoryActionState.Error(
                            message = "That category already exists.",
                        )
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                CategoryActionState.Error(
                    message = "Category could not be created.",
                )
            }
        }
    }

    fun resetActionState() {
        _actionState.value = CategoryActionState.Idle
    }

    companion object {
        fun factory(
            repository: CategoryRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CategoryViewModel(repository)
                }
            }
    }
}