package com.aleon.proyectocellcli.ui.screens

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.aleon.proyectocellcli.domain.model.CategorySpending
import com.aleon.proyectocellcli.ui.MainViewModel
import com.aleon.proyectocellcli.ui.viewmodel.HomeViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val currency by mainViewModel.currency.collectAsState()
    val currencySymbol = remember(currency) {
        currency.substringAfter("(").substringBefore(")")
    }
    val categorySpending by homeViewModel.categorySpending.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChartCard(categorySpending = categorySpending)
        Spacer(modifier = Modifier.height(16.dp))
        TimeframeSelector(homeViewModel = homeViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        CategoryTotalsList(
            categorySpending = categorySpending,
            currencySymbol = currencySymbol
        )
    }
}

@Composable
fun ChartCard(categorySpending: List<CategorySpending>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Resumen de Gastos", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            DonutChart(categorySpending = categorySpending)
        }
    }
}

@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    categorySpending: List<CategorySpending>
) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            PieChart(context).apply {
                isDrawHoleEnabled = true
                holeRadius = 58f
                transparentCircleRadius = 61f
                setUsePercentValues(true)
                description.isEnabled = false
                legend.isEnabled = false
                setEntryLabelColor(textColor)
                setEntryLabelTextSize(12f)
                centerText = "Gastos"
                setCenterTextSize(24f)
                setCenterTextColor(textColor)
            }
        },
        update = { chart ->
            val entries = categorySpending.map { PieEntry(it.total.toFloat(), it.category.name) }
            val colors = categorySpending.map { it.category.color.toArgb() }

            val dataSet = PieDataSet(entries, "Categorías")
            dataSet.colors = colors
            dataSet.valueFormatter = PercentFormatter(chart)
            dataSet.valueTextSize = 12f
            dataSet.valueTextColor = Color.BLACK
            dataSet.sliceSpace = 3f

            chart.data = PieData(dataSet)
            chart.animateY(1000)
            chart.invalidate()
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeframeSelector(homeViewModel: HomeViewModel) {
    var showDayPicker by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }
    var showPeriodPicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        OutlinedButton(onClick = { showDayPicker = true }) { Text("Día") }
        OutlinedButton(onClick = { showMonthPicker = true }) { Text("Mes") }
        OutlinedButton(onClick = { showYearPicker = true }) { Text("Año") }
        OutlinedButton(onClick = { showPeriodPicker = true }) { Text("Periodo") }
    }

    if (showDayPicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDayPicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                        homeViewModel.setDayFilter(date)
                    }
                    showDayPicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showDayPicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }

    if (showMonthPicker) {
        MonthPickerDialog(
            onDismiss = { showMonthPicker = false },
            onMonthSelected = { month, year ->
                homeViewModel.setMonthFilter(month, year)
                showMonthPicker = false
            }
        )
    }

    if (showYearPicker) {
        YearPickerDialog(
            onDismiss = { showYearPicker = false },
            onYearSelected = { year ->
                homeViewModel.setYearFilter(year)
                showYearPicker = false
            }
        )
    }

    if (showPeriodPicker) {
        val dateRangePickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showPeriodPicker = false },
            confirmButton = {
                Button(
                    enabled = dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null,
                    onClick = {
                        val startDate = Instant.ofEpochMilli(dateRangePickerState.selectedStartDateMillis!!).atZone(ZoneOffset.UTC).toLocalDate()
                        val endDate = Instant.ofEpochMilli(dateRangePickerState.selectedEndDateMillis!!).atZone(ZoneOffset.UTC).toLocalDate()
                        homeViewModel.setPeriodFilter(startDate, endDate)
                        showPeriodPicker = false
                    }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showPeriodPicker = false }) { Text("Cancelar") } }
        ) { DateRangePicker(state = dateRangePickerState, modifier = Modifier.height(500.dp)) }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthPickerDialog(onDismiss: () -> Unit, onMonthSelected: (Month, Int) -> Unit) {
    val months = Month.values()
    var year by remember { mutableStateOf(LocalDate.now().year) }
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { year-- }) { Icon(Icons.Default.ArrowBack, "Año anterior") }
                    Text(text = year.toString(), style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { year++ }) { Icon(Icons.Default.ArrowForward, "Año siguiente") }
                }
                LazyColumn {
                    items(months) { month ->
                        Text(
                            text = month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.uppercase() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMonthSelected(month, year) }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun YearPickerDialog(onDismiss: () -> Unit, onYearSelected: (Int) -> Unit) {
    var year by remember { mutableStateOf(LocalDate.now().year) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Año") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { year-- }) { Icon(Icons.Default.ArrowBack, "Año anterior") }
                Text(text = year.toString(), style = MaterialTheme.typography.headlineMedium)
                IconButton(onClick = { year++ }) { Icon(Icons.Default.ArrowForward, "Año siguiente") }
            }
        },
        confirmButton = { Button(onClick = { onYearSelected(year) }) { Text("Aceptar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun CategoryTotalsList(categorySpending: List<CategorySpending>, currencySymbol: String) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Totales por Categoría",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(categorySpending) { spending ->
            CategoryTotalItem(
                categorySpending = spending,
                currencySymbol = currencySymbol
            )
        }
    }
}

@Composable
fun CategoryTotalItem(categorySpending: CategorySpending, currencySymbol: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(categorySpending.category.color, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = categorySpending.category.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$currencySymbol${"%.2f".format(categorySpending.total)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )
        }
    }
}
