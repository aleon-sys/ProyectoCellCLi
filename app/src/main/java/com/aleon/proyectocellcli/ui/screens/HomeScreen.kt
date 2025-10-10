package com.aleon.proyectocellcli.ui.screens

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.aleon.proyectocellcli.domain.model.CategorySpending
import com.aleon.proyectocellcli.ui.MainViewModel
import com.aleon.proyectocellcli.ui.viewmodel.HomeViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

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
        ChartCard()
        Spacer(modifier = Modifier.height(16.dp))
        TimeframeSelector()
        Spacer(modifier = Modifier.height(16.dp))
        CategoryTotalsList(
            categorySpending = categorySpending,
            currencySymbol = currencySymbol
        )
    }
}

@Composable
fun ChartCard() {
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
            DonutChart() // This will be updated in Step 2
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
            // Dummy data for now
            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(40f, "Comida"))
            entries.add(PieEntry(25f, "Transporte"))
            entries.add(PieEntry(15f, "Ocio"))

            val dataSet = PieDataSet(entries, "Categorías")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueFormatter = PercentFormatter(chart)
            dataSet.valueTextSize = 12f
            dataSet.valueTextColor = Color.BLACK
            dataSet.sliceSpace = 3f

            chart.data = PieData(dataSet)
            chart.animateY(1400)
            chart.invalidate()
        }
    )
}

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

@Composable
fun CategoryTotalsList(
    categorySpending: List<CategorySpending>,
    currencySymbol: String
) {
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
fun CategoryTotalItem(
    categorySpending: CategorySpending,
    currencySymbol: String
) {
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // HomeScreen() // Preview won't work easily with Hilt
}
