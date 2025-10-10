package com.aleon.proyectocellcli.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.use_case.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed class AddOutlayEvent {
    data object ExpenseSavedAndClose : AddOutlayEvent()
    data object ExpenseSavedAndContinue : AddOutlayEvent()
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AddOutlayViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase,
    getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val getExpenseByIdUseCase: GetExpenseByIdUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount = _amount.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    val categories = getCategoriesUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    private val _eventFlow = MutableSharedFlow<AddOutlayEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val expenseId: Long = savedStateHandle.get<Long>("expenseId") ?: -1L
    private var currentExpense: Expense? = null

    init {
        viewModelScope.launch {
            categories.collect { categoryList ->
                if (expenseId == -1L && _selectedCategory.value == null && categoryList.isNotEmpty()) {
                    _selectedCategory.value = categoryList.first()
                }
            }
        }

        if (expenseId != -1L) {
            loadExpenseDetails()
        }
    }

    private fun loadExpenseDetails() {
        viewModelScope.launch {
            getExpenseByIdUseCase(expenseId).filterNotNull().first().let { expense ->
                currentExpense = expense
                _description.value = expense.description
                _amount.value = String.format("%.2f", expense.amount)
                _selectedDate.value = expense.date
                _selectedCategory.value = expense.category
            }
        }
    }

    private fun clearFields() {
        _description.value = ""
        _amount.value = ""
    }

    fun onDescriptionChange(newDescription: String) { _description.value = newDescription }
    fun onAmountChange(newAmount: String) {
        if (newAmount.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
            _amount.value = newAmount
        }
    }
    fun onDateChange(newDate: LocalDate) { _selectedDate.value = newDate }
    fun onCategoryChange(newCategory: Category) { _selectedCategory.value = newCategory }

    fun onSaveExpense() {
        viewModelScope.launch {
            val expenseToSave = Expense(
                id = currentExpense?.id ?: 0L.toInt(),
                description = _description.value.trim(),
                amount = _amount.value.toDoubleOrNull() ?: 0.0,
                category = _selectedCategory.value!!,
                date = _selectedDate.value
            )

            if (currentExpense != null) {
                updateExpenseUseCase(expenseToSave)
                _eventFlow.emit(AddOutlayEvent.ExpenseSavedAndClose)
            } else {
                addExpenseUseCase(expenseToSave)
                clearFields()
                _eventFlow.emit(AddOutlayEvent.ExpenseSavedAndContinue)
            }
        }
    }

    fun onAddCategory(name: String, color: Color) {
        viewModelScope.launch {
            addCategoryUseCase(Category(name = name, color = color))
        }
    }

    fun onUpdateCategory(category: Category) {
        viewModelScope.launch {
            updateCategoryUseCase(category)
        }
    }
}