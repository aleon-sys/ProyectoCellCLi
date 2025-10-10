package com.aleon.proyectocellcli.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.ui.MainViewModel
import com.aleon.proyectocellcli.ui.navigation.Screen
import com.aleon.proyectocellcli.ui.viewmodel.OutlayViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OutlayScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: OutlayViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val expensesByDate by viewModel.expensesByDate.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currency by mainViewModel.currency.collectAsState()
    val currencySymbol = remember(currency) {
        currency.substringAfter("(").substringBefore(")")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar gastos...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            shape = RoundedCornerShape(50),
            textStyle = TextStyle(fontSize = 18.sp), // Increased font size
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        
        Text(
            text = "Historial de Gastos",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            expensesByDate.forEach { (date, expenses) ->
                stickyHeader {
                    DateHeader(date = date)
                }
                items(expenses, key = { it.id }) { expense ->
                    ExpenseItem(
                        expense = expense,
                        currencySymbol = currencySymbol,
                        onEditClick = {
                            navController.navigate(Screen.AddOutlay.createRoute(expense.id.toLong()))
                        },
                        onDeleteClick = {
                            viewModel.onDeleteExpense(expense)
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateHeader(date: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM")
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
fun ExpenseItem(
    expense: Expense,
    currencySymbol: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
                    fontWeight = FontWeight.SemiBold, // Bolder
                    fontSize = 17.sp // Bigger
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(expense.category.color, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = expense.category.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "$currencySymbol${"%.2f".format(expense.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold, // Bolder
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 18.sp // Bigger
            )
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar Gasto")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar Gasto")
                }
            }
        }
    }
}