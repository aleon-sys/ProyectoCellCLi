package com.aleon.proyectocellcli.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data class moved to the top level to be accessible throughout the file
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

// 1. Composable for the Chart (Replaced with a basic Compose simulation)
@Composable
fun ChartCard() {
    val chartData = remember { mapOf(
        "Comida" to 4f,
        "Transporte" to 2f,
        "Ocio" to 1f,
        "Hogar" to 3f
    )}
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primaryContainer
    )
    val maxVal = chartData.values.maxOrNull() ?: 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumen de Gastos", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                chartData.values.forEachIndexed { index, value ->
                    Bar(
                        value = value,
                        maxValue = maxVal,
                        color = colors.getOrElse(index) { Color.Gray }
                    )
                }
            }
        }
    }
}

@Composable
fun Bar(value: Float, maxValue: Float, color: Color) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .fillMaxHeight(fraction = value / maxValue)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(color)
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