package com.aleon.proyectocellcli.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.use_case.AddCategoryUseCase
import com.aleon.proyectocellcli.domain.use_case.AddExpenseUseCase
import com.aleon.proyectocellcli.domain.use_case.GetCategoriesUseCase
import com.aleon.proyectocellcli.domain.use_case.UpdateCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddOutlayViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        getCategoriesUseCase().onEach { categoryList ->
            _categories.value = categoryList
        }.launchIn(viewModelScope)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSaveExpense(description: String, amount: String, category: Category) {
        val amountDouble = amount.toDoubleOrNull()
        if (description.isBlank() || amountDouble == null || amountDouble <= 0) {
            // TODO: Handle validation error
            return
        }

        viewModelScope.launch {
            addExpenseUseCase(
                Expense(
                    description = description,
                    amount = amountDouble,
                    date = LocalDate.now(),
                    category = category
                )
            )
        }
    }
}