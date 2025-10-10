package com.aleon.proyectocellcli.ui.screens

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

import com.aleon.proyectocellcli.ui.MainViewModel

// Data class for the list items
data class CategoryTotal(val name: String, val amount: Double)

// Main Composable for the Home Screen
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val currency by mainViewModel.currency.collectAsState()
    val currencySymbol = remember(currency) {
        currency.substringAfter("(").substringBefore(")")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChartCard()
        Spacer(modifier = Modifier.height(16.dp))
        TimeframeSelector()
        Spacer(modifier = Modifier.height(16.dp))
        CategoryTotalsList(currencySymbol = currencySymbol)
    }
}

@Composable
fun ChartCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp), // Adjust height as needed
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Resumen de Gastos", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            DonutChart()
        }
    }
}

@Composable
fun DonutChart(modifier: Modifier = Modifier) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            PieChart(context).apply {
                // Basic setup
                isDrawHoleEnabled = true
                holeRadius = 58f
                transparentCircleRadius = 61f
                setUsePercentValues(true)
                description.isEnabled = false
                legend.isEnabled = false
                setEntryLabelColor(textColor)
                setEntryLabelTextSize(12f)

                // Center text
                centerText = "Gastos"
                setCenterTextSize(24f)
                setCenterTextColor(textColor)
            }
        },
        update = { chart ->
            // Create dummy data entries
            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(40f, "Comida"))
            entries.add(PieEntry(25f, "Transporte"))
            entries.add(PieEntry(15f, "Ocio"))
            entries.add(PieEntry(10f, "Hogar"))
            entries.add(PieEntry(10f, "Otros"))

            val dataSet = PieDataSet(entries, "Categorías de Gastos")

            // Configure colors
            val colors = ArrayList<Int>()
            for (c in ColorTemplate.MATERIAL_COLORS) colors.add(c)
            for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
            dataSet.colors = colors

            // Configure data set properties
            dataSet.valueFormatter = PercentFormatter(chart)
            dataSet.valueTextSize = 12f
            dataSet.valueTextColor = Color.BLACK
            dataSet.sliceSpace = 3f

            val data = PieData(dataSet)
            chart.data = data

            // Animate and refresh
            chart.animateY(1400)
            chart.invalidate()
        }
    )
}


// 2. Composable for the Day/Week/Month selector
@Composable
fun TimeframeSelector() {
    var selectedTimeframe by remember { mutableStateOf("Mes") }
    val timeframes = listOf("Día", "Semana", "Mes")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        timeframes.forEach { timeframe ->
            OutlinedButton(onClick = { selectedTimeframe = timeframe }) {
                Text(text = timeframe)
            }
        }
    }
}

// 3. Composable for the list of category totals
@Composable
fun CategoryTotalsList(currencySymbol: String) {
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
            CategoryTotalItem(category = category, currencySymbol = currencySymbol)
        }
    }
}

@Composable
fun CategoryTotalItem(category: CategoryTotal, currencySymbol: String) {
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
                text = "$currencySymbol${"%.2f".format(category.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}