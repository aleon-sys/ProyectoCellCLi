package com.aleon.proyectocellcli.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData

// Data class for the list items
data class CategoryTotal(val name: String, val amount: Double)

// Main Composable for the Home Screen
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
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
        CategoryTotalsList()
    }
}

// 1. Composable for the Donut Chart using ycharts (STABLE VERSION)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChartCard() {
    val pieChartData = PieChartData(
        slices = listOf(
            PieChartData.Slice("Comida", 4f, MaterialTheme.colorScheme.primary),
            PieChartData.Slice("Transporte", 2f, MaterialTheme.colorScheme.secondary),
            PieChartData.Slice("Ocio", 1f, MaterialTheme.colorScheme.tertiary),
            PieChartData.Slice("Hogar", 3f, MaterialTheme.colorScheme.primaryContainer)
        ),
        plotType = PlotType.Donut
    )

    val pieChartConfig = PieChartConfig(
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Resumen de Gastos", style = MaterialTheme.typography.titleMedium)
            
            BoxWithConstraints(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val chartSize = minOf(maxWidth, maxHeight)
                PieChart(
                    modifier = Modifier.size(chartSize),
                    pieChartData = pieChartData,
                    pieChartConfig = pieChartConfig
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            ChartLegend(slices = pieChartData.slices)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChartLegend(slices: List<PieChartData.Slice>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        slices.forEach { slice ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(slice.color, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = slice.label, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
