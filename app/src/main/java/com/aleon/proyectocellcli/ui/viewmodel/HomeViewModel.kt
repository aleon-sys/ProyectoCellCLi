package com.aleon.proyectocellcli.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.domain.model.CategorySpending
import com.aleon.proyectocellcli.domain.use_case.GetAllCategorySpendingUseCase
import com.aleon.proyectocellcli.domain.use_case.GetCategorySpendingForDateRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.Month
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class DateFilter(
    val startDate: LocalDate,
    val endDate: LocalDate
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllCategorySpendingUseCase: GetAllCategorySpendingUseCase,
    private val getCategorySpendingForDateRangeUseCase: GetCategorySpendingForDateRangeUseCase
) : ViewModel() {

    private val _dateFilter = MutableStateFlow<DateFilter?>(null)

    val categorySpending: StateFlow<List<CategorySpending>> = _dateFilter.flatMapLatest { filter ->
        if (filter != null) {
            getCategorySpendingForDateRangeUseCase(filter.startDate, filter.endDate)
        } else {
            // By default, show data for the current month
            val today = LocalDate.now()
            val startDate = today.with(TemporalAdjusters.firstDayOfMonth())
            val endDate = today.with(TemporalAdjusters.lastDayOfMonth())
            getCategorySpendingForDateRangeUseCase(startDate, endDate)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setDayFilter(date: LocalDate) {
        _dateFilter.value = DateFilter(date, date)
    }

    fun setMonthFilter(month: Month, year: Int) {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.with(TemporalAdjusters.lastDayOfMonth())
        _dateFilter.value = DateFilter(startDate, endDate)
    }

    fun setYearFilter(year: Int) {
        val startDate = LocalDate.of(year, Month.JANUARY, 1)
        val endDate = LocalDate.of(year, Month.DECEMBER, 31)
        _dateFilter.value = DateFilter(startDate, endDate)
    }

    fun setPeriodFilter(startDate: LocalDate, endDate: LocalDate) {
        _dateFilter.value = DateFilter(startDate, endDate)
    }
}