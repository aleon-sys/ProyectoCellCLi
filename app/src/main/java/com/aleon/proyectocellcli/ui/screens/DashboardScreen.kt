package com.aleon.proyectocellcli.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// --- Data Model for the Screen ---
data class Expense(
    val id: Int,
    val description: String,
    val amount: Double,
    val category: String,
    val date: LocalDate
)

// --- Main Composable ---
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    // --- Sample Data ---
    val sampleExpenses = remember {
        listOf(
            Expense(1, "Café y croissant", 5.50, "Comida", LocalDate.now()),
            Expense(2, "Ticket de metro", 2.40, "Transporte", LocalDate.now()),
            Expense(3, "Cine", 12.00, "Ocio", LocalDate.now().minusDays(1)),
            Expense(4, "Factura de la luz", 75.20, "Hogar", LocalDate.now().minusDays(1)),
            Expense(5, "Supermercado", 62.30, "Comida", LocalDate.now().minusDays(1)),
            Expense(6, "Gasolina", 50.00, "Transporte", LocalDate.now().minusDays(2))
        ).groupBy { it.date } // Group expenses by date
    }
    
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar gastos...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            shape = RoundedCornerShape(50), // Oval shape
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant, // Greyish border
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        
        Text(
            text = "Historial de Gastos",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            sampleExpenses.forEach { (date, expenses) ->
                stickyHeader {
                    DateHeader(date = date)
                }
                items(expenses) { expense ->
                    ExpenseItem(expense = expense)
                }
            }
        }
    }
}

// --- Child Composables ---

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateHeader(date: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", Locale("es", "ES"))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Text(
            text = date.format(formatter).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = expense.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$${"%.2f".format(expense.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Row {
                IconButton(onClick = { /* TODO: Edit logic */ }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar Gasto")
                }
                IconButton(onClick = { /* TODO: Delete logic */ }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar Gasto")
                }
            }
        }
    }
}

// --- Preview ---
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen()
}
