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
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.hilt.navigation.compose.hiltViewModel
import com.aleon.proyectocellcli.ui.viewmodel.CategoryTotal
import com.aleon.proyectocellcli.ui.viewmodel.HomeViewModel


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
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val categoryTotals by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    
    var activeFilter by remember { mutableStateOf(TimeframeType.DAY) }
    var showDialog by remember { mutableStateOf<TimeframeType?>(null) }
    
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val selectedDateText = selectedDate.format(dateFormatter)

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
            TimeframeButton("Mes", activeFilter == TimeframeType.MONTH) { /* TODO */ }
            TimeframeButton("Año", activeFilter == TimeframeType.YEAR) { /* TODO */ }
            TimeframeButton("Periodo", activeFilter == TimeframeType.PERIOD) { /* TODO */ }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = selectedDateText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Chart Section ---
        ChartCard(categoryTotals = categoryTotals)
        Spacer(modifier = Modifier.height(16.dp))
        
        // --- Totals List Section ---
        CategoryTotalsList(categoryTotals = categoryTotals)
    }

    // --- Dialogs ---
    if (showDialog == TimeframeType.DAY) {
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
    // TODO: Implement other dialogs
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
    val valueTextColor = MaterialTheme.colorScheme.onSurface.toArgb() // Read color in Composable scope

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        if (categoryTotals.isEmpty() || totalAmount == 0f) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay gastos para este día", textAlign = TextAlign.Center)
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
                    val entries = categoryTotals.map { PieEntry((it.amount / totalAmount * 100).toFloat(), it.name) }
                    val dataSet = PieDataSet(entries, "").apply {
                        this.colors = colors
                        this.valueFormatter = PercentageFormatter()
                        this.valueTextSize = 12f
                        this.valueTextColor = valueTextColor // Use the variable here
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
        // You can show a message here as well if you want, or just an empty space.
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

// Previews might need adjustments to work with ViewModels,
// but the core logic is now in place.
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // This preview will show the empty state
    // HomeScreen() 
}
