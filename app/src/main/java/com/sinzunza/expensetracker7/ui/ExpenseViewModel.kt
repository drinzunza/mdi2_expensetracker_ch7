package com.sinzunza.expensetracker7.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sinzunza.expensetracker7.data.Expense
import com.sinzunza.expensetracker7.data.ExpenseRepository
import com.sinzunza.expensetracker7.data.ExpenseWithCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import java.time.YearMonth

sealed interface ExpenseListUiState {
    data object Loading : ExpenseListUiState
    data class Content(
        val expenses: List<ExpenseWithCategory>,
        val totalCents: Long,
        val month: YearMonth,
    ) : ExpenseListUiState
    data class Error(val message: String) : ExpenseListUiState
}

sealed interface EditorUiState {
    data object Idle : EditorUiState
    data object Saving : EditorUiState
    data object Saved : EditorUiState
    data class Error(val message: String) : EditorUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModel(
    private val repository: ExpenseRepository,
) : ViewModel() {
    private val currentMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<ExpenseListUiState> = currentMonth
        .flatMapLatest { month ->
            repository.observeMonth(month).map<List<ExpenseWithCategory>, ExpenseListUiState> { expenses ->
                ExpenseListUiState.Content(
                    expenses = expenses,
                    totalCents = expenses.sumOf { it.expense.amountCents },
                    month = month,
                )
            }
        }
        .catch { emit(ExpenseListUiState.Error("Expenses could not be loaded")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExpenseListUiState.Loading,
        )

    private val _editorState = MutableStateFlow<EditorUiState>(EditorUiState.Idle)
    val editorState: StateFlow<EditorUiState> = _editorState.asStateFlow()

    fun observeExpense(id: Long): Flow<ExpenseWithCategory?> =
        repository.observeExpense(id)

    fun refreshMonth() {
        currentMonth.value = YearMonth.now()
    }

    fun resetEditorState() {
        _editorState.value = EditorUiState.Idle
    }

    fun save(expense: Expense) {
        if (_editorState.value == EditorUiState.Saving) return
        _editorState.value = EditorUiState.Saving
        viewModelScope.launch {
            _editorState.value = try {
                repository.save(expense)
                EditorUiState.Saved
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                EditorUiState.Error("Could not save. Please try again.")
            }
        }
    }

    fun delete(expense: Expense) {
        if (_editorState.value == EditorUiState.Saving) return
        _editorState.value = EditorUiState.Saving
        viewModelScope.launch {
            _editorState.value = try {
                repository.delete(expense)
                EditorUiState.Saved
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                EditorUiState.Error("Could not delete. Please try again.")
            }
        }
    }

    companion object {
        fun factory(repository: ExpenseRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { ExpenseViewModel(repository) }
            }
    }
}
