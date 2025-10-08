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
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// Data class for the list items
data class CategoryTotal(val name: String, val amount: Double)

// This class formats the value on the chart slice into a percentage string
private class PercentageFormatter : ValueFormatter() {
    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        return "${value.toInt()}%"
    }
}

private enum class TimeframeType {
    DAY, MONTH, YEAR, PERIOD
}

// Main Composable for the Home Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var activeFilter by remember { mutableStateOf(TimeframeType.MONTH) }
    var showDialog by remember { mutableStateOf<TimeframeType?>(null) }
    var selectedDateText by remember { mutableStateOf("Octubre 2025") }

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
            TimeframeButton("Día", activeFilter == TimeframeType.DAY) { showDialog = TimeframeType.DAY }
            TimeframeButton("Mes", activeFilter == TimeframeType.MONTH) { showDialog = TimeframeType.MONTH }
            TimeframeButton("Año", activeFilter == TimeframeType.YEAR) { showDialog = TimeframeType.YEAR }
            TimeframeButton("Periodo", activeFilter == TimeframeType.PERIOD) { showDialog = TimeframeType.PERIOD }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = selectedDateText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Chart Section ---
        ChartCard()
        Spacer(modifier = Modifier.height(16.dp))
        
        // --- Totals List Section ---
        CategoryTotalsList()
    }

    // --- Dialogs ---
    if (showDialog != null) {
        when (showDialog) {
            TimeframeType.DAY -> {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDialog = null },
                    confirmButton = {
                        Button(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                                selectedDateText = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                                activeFilter = TimeframeType.DAY
                            }
                            showDialog = null
                        }) { Text("Aceptar") }
                    }
                ) { DatePicker(state = datePickerState) }
            }
            TimeframeType.MONTH -> {
                MonthPickerDialog(
                    onDismiss = { showDialog = null },
                    onMonthSelected = { month ->
                        selectedDateText = "$month ${LocalDate.now().year}"
                        activeFilter = TimeframeType.MONTH
                        showDialog = null
                    }
                )
            }
            TimeframeType.YEAR -> {
                YearPickerDialog(
                    onDismiss = { showDialog = null },
                    onYearSelected = { year ->
                        selectedDateText = year.toString()
                        activeFilter = TimeframeType.YEAR
                        showDialog = null
                    }
                )
            }
            TimeframeType.PERIOD -> {
                val dateRangePickerState = rememberDateRangePickerState()
                Dialog(onDismissRequest = { showDialog = null }) {
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DateRangePicker(state = dateRangePickerState)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showDialog = null }) { Text("Cancelar") }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = {
                                    dateRangePickerState.selectedStartDateMillis?.let { startMillis ->
                                        dateRangePickerState.selectedEndDateMillis?.let { endMillis ->
                                            val startDate = Instant.ofEpochMilli(startMillis).atZone(ZoneOffset.UTC).toLocalDate()
                                            val endDate = Instant.ofEpochMilli(endMillis).atZone(ZoneOffset.UTC).toLocalDate()
                                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
                                            selectedDateText = "${startDate.format(formatter)} - ${endDate.format(formatter)}"
                                            activeFilter = TimeframeType.PERIOD
                                        }
                                    }
                                    showDialog = null
                                }) { Text("Aceptar") }
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun TimeframeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val colors = if (isSelected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
    Button(
        onClick = onClick,
        colors = colors,
        border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder
    ) {
        Text(text)
    }
}

// 1. Composable for the Donut Chart using MPAndroidChart via AndroidView
@Composable
fun ChartCard() {
    val data = remember {
        mapOf(
            "Comida" to 40f,
            "Transporte" to 20f,
            "Ocio" to 10f,
            "Hogar" to 30f
        )
    }
    val colors = listOf(
        MaterialTheme.colorScheme.primary.toArgb(),
        MaterialTheme.colorScheme.secondary.toArgb(),
        MaterialTheme.colorScheme.tertiary.toArgb(),
        MaterialTheme.colorScheme.primaryContainer.toArgb()
    )
    val valueTextColor = MaterialTheme.colorScheme.onSurface.toArgb()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
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
                val entries = data.entries.map { PieEntry(it.value, it.key) }
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

// 3. Composable for the list of category totals
@Composable
fun CategoryTotalsList() {
    val sampleCategories = remember {
        listOf(
            CategoryTotal("Comida", 450.75),
            CategoryTotal("Transporte", 120.50),
            CategoryTotal("Ocio", 85.00),
            CategoryTotal("Hogar", 320.00),
            CategoryTotal("Salud", 50.25)
        )
    }

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
        items(sampleCategories) { category ->
            CategoryTotalItem(category = category)
        }
    }
}

@Composable
fun CategoryTotalItem(category: CategoryTotal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$${"%.2f".format(category.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun MonthPickerDialog(onDismiss: () -> Unit, onMonthSelected: (String) -> Unit) {
    val months = remember {
        listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            LazyColumn(modifier = Modifier.padding(vertical = 16.dp)) {
                items(months) { month ->
                    Text(
                        text = month,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMonthSelected(month) }
                            .padding(horizontal = 32.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun YearPickerDialog(onDismiss: () -> Unit, onYearSelected: (Int) -> Unit) {
    var year by remember { mutableStateOf(LocalDate.now().year) }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { year-- }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Year")
                    }
                    Text(text = year.toString(), style = MaterialTheme.typography.headlineMedium)
                    IconButton(onClick = { year++ }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Year")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onYearSelected(year) }) {
                    Text("Aceptar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
