package com.aleon.proyectocellcli.ui.screens

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.hilt.navigation.compose.hiltViewModel
import com.aleon.proyectocellcli.ui.viewmodel.CategoryTotal
import com.aleon.proyectocellcli.ui.viewmodel.HomeViewModel
import com.aleon.proyectocellcli.ui.viewmodel.Timeframe


// This class formats the value on the chart slice into a percentage string
private class PercentageFormatter : ValueFormatter() {
    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        return "${value.toInt()}%"
    }
}

// Main Composable for the Home Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filterState by viewModel.dateFilterState.collectAsState()
    var showDialog by remember { mutableStateOf<Timeframe?>(null) }

    val selectedDateText = remember(filterState) {
        when (filterState.timeframe) {
            Timeframe.DAY -> filterState.startDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            Timeframe.MONTH -> "${filterState.startDate.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.uppercase() }} ${filterState.startDate.year}"
            Timeframe.YEAR -> filterState.startDate.year.toString()
            Timeframe.PERIOD -> {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
                "${filterState.startDate.format(formatter)} - ${filterState.endDate?.format(formatter)}"
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Date Filter Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TimeframeButton("Día", filterState.timeframe == Timeframe.DAY) { showDialog = Timeframe.DAY }
            TimeframeButton("Mes", filterState.timeframe == Timeframe.MONTH) { showDialog = Timeframe.MONTH }
            TimeframeButton("Año", filterState.timeframe == Timeframe.YEAR) { showDialog = Timeframe.YEAR }
            TimeframeButton("Periodo", filterState.timeframe == Timeframe.PERIOD) { showDialog = Timeframe.PERIOD }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = selectedDateText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Chart Section ---
        ChartCard(categoryTotals = uiState)
        Spacer(modifier = Modifier.height(16.dp))
        
        // --- Totals List Section ---
        CategoryTotalsList(categoryTotals = uiState)
    }

    // --- Dialogs ---
    if (showDialog != null) {
        when (showDialog) {
            Timeframe.DAY -> {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDialog = null },
                    confirmButton = {
                        Button(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val newDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                                viewModel.onDateSelected(newDate)
                            }
                            showDialog = null
                        }) { Text("Aceptar") }
                    }
                ) { DatePicker(state = datePickerState) }
            }
            Timeframe.MONTH -> {
                MonthPickerDialog(
                    onDismiss = { showDialog = null },
                    onMonthSelected = { month, year ->
                        viewModel.onMonthSelected(month, year)
                        showDialog = null
                    }
                )
            }
            Timeframe.YEAR -> {
                YearPickerDialog(
                    onDismiss = { showDialog = null },
                    onYearSelected = { year ->
                        viewModel.onYearSelected(year)
                        showDialog = null
                    }
                )
            }
            Timeframe.PERIOD -> {
                PeriodPickerDialog(
                    onDismiss = { showDialog = null },
                    onPeriodSelected = { startDate, endDate ->
                        viewModel.onPeriodSelected(startDate, endDate)
                        showDialog = null
                    }
                )
            }

            null -> TODO()
        }
    }
}

@Composable
private fun TimeframeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = if (isSelected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
    Button(onClick = onClick, colors = colors, border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder) {
        Text(text)
    }
}

// 1. Composable for the Donut Chart - NOW DYNAMIC
@Composable
fun ChartCard(categoryTotals: List<CategoryTotal>) {
    val totalAmount = categoryTotals.sumOf { it.amount }.toFloat()
    val colors = categoryTotals.map { it.color.toArgb() }
    val valueTextColor = MaterialTheme.colorScheme.onSurface.toArgb()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        if (categoryTotals.isEmpty() || totalAmount == 0f) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay gastos para este periodo", textAlign = TextAlign.Center)
            }
        } else {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    PieChart(context).apply {
                        isDrawHoleEnabled = true
                        holeRadius = 70f
                        setHoleColor(AndroidColor.TRANSPARENT)
                        description.isEnabled = false
                        setDrawEntryLabels(false)
                        legend.isEnabled = true
                        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                        legend.orientation = Legend.LegendOrientation.HORIZONTAL
                        legend.setDrawInside(false)
                    }
                },
                update = { chart ->
                    val entries = categoryTotals
                        .filter { it.amount > 0 }
                        .map { PieEntry((it.amount / totalAmount * 100).toFloat(), it.name) }
                    
                    val dataSet = PieDataSet(entries, "").apply {
                        this.colors = colors
                        this.valueFormatter = PercentageFormatter()
                        this.valueTextSize = 12f
                        this.valueTextColor = valueTextColor
                    }
                    val pieData = PieData(dataSet)
                    chart.data = pieData
                    chart.animateY(1000)
                    chart.invalidate()
                }
            )
        }
    }
}

// 3. Composable for the list of category totals
@Composable
fun CategoryTotalsList(categoryTotals: List<CategoryTotal>) {
    if (categoryTotals.isEmpty() || categoryTotals.all { it.amount == 0.0 }) {
        // Handled by the ChartCard's empty message
    } else {
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
            items(categoryTotals.filter { it.amount > 0 }) { categoryTotal ->
                CategoryTotalItem(categoryTotal = categoryTotal)
            }
        }
    }
}

@Composable
fun CategoryTotalItem(categoryTotal: CategoryTotal) {
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
                    .size(16.dp)
                    .background(categoryTotal.color, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = categoryTotal.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$${"%.2f".format(categoryTotal.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun MonthPickerDialog(onDismiss: () -> Unit, onMonthSelected: (month: Month, year: Int) -> Unit) {
    val currentYear = LocalDate.now().year
    var year by remember { mutableStateOf(currentYear) }
    val months = Month.values()

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    IconButton(onClick = { year-- }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null) }
                    Text(text = year.toString(), style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { year++ }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
                }
                LazyColumn(modifier = Modifier.padding(vertical = 16.dp)) {
                    items(months) { month ->
                        Text(
                            text = month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.uppercase() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMonthSelected(month, year) }
                                .padding(horizontal = 32.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodPickerDialog(
    onDismiss: () -> Unit,
    onPeriodSelected: (startDate: LocalDate, endDate: LocalDate) -> Unit
) {
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isPickingStartDate by remember { mutableStateOf(true) }

    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Seleccionar Periodo", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(24.dp))
                Box {
                    OutlinedTextField(value = startDate?.format(formatter) ?: "Fecha de Inicio", onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth())
                    Box(modifier = Modifier.matchParentSize().clickable { isPickingStartDate = true; showDatePicker = true })
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedTextField(value = endDate?.format(formatter) ?: "Fecha de Fin", onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth())
                    Box(modifier = Modifier.matchParentSize().clickable { isPickingStartDate = false; showDatePicker = true })
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (startDate != null && endDate != null) { onPeriodSelected(startDate!!, endDate!!) } },
                        enabled = startDate != null && endDate != null
                    ) { Text("Aceptar") }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        if (isPickingStartDate) startDate = selectedDate else endDate = selectedDate
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
private fun YearPickerDialog(onDismiss: () -> Unit, onYearSelected: (Int) -> Unit) {
    var year by remember { mutableStateOf(LocalDate.now().year) }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { year-- }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null) }
                    Text(text = year.toString(), style = MaterialTheme.typography.headlineMedium)
                    IconButton(onClick = { year++ }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onYearSelected(year) }) { Text("Aceptar") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // HomeScreen() // Preview won't work easily with Hilt ViewModel
}
