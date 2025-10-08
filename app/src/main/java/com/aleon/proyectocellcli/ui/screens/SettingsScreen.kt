package com.aleon.proyectocellcli.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.aleon.proyectocellcli.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val selectedTheme by viewModel.theme.collectAsState()
    val selectedCurrency by viewModel.currency.collectAsState()
    val monthlyLimit by viewModel.monthlyLimit.collectAsState()
    
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showMonthlyLimitDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // --- Theme Selection ---
        SettingSection(title = "Tema de la Aplicación") {
            ThemeSelector(
                selectedTheme = selectedTheme,
                onThemeSelected = { viewModel.onThemeSelected(it) }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // --- Currency Selection ---
        SettingSection(title = "Moneda") {
            SettingItem(
                title = "Moneda Principal",
                subtitle = selectedCurrency,
                onClick = { showCurrencyDialog = true }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // --- Monthly Limit ---
        SettingSection(title = "Límite Mensual") {
            val currencySymbol = selectedCurrency.substringAfter("(").substringBefore(")")
            SettingItem(
                title = "Establecer límite de gastos",
                subtitle = if (monthlyLimit > 0) "$currencySymbol${"%.2f".format(monthlyLimit)}" else "No establecido",
                onClick = { showMonthlyLimitDialog = true }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // --- Data Management ---
        SettingSection(title = "Gestión de Datos") {
            Button(
                onClick = { showDeleteConfirmation = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Borrar Todos los Gastos")
            }
        }
    }

    // --- Dialogs ---
    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.onDeleteAllExpenses()
                showDeleteConfirmation = false
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }

    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            onCurrencySelected = {
                viewModel.onCurrencySelected(it)
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }

    if (showMonthlyLimitDialog) {
        MonthlyLimitDialog(
            initialValue = if (monthlyLimit > 0) monthlyLimit.toString() else "",
            onDismiss = { showMonthlyLimitDialog = false },
            onSave = { newLimit ->
                viewModel.onSetMonthlyLimit(newLimit)
                showMonthlyLimitDialog = false
            }
        )
    }
}

// --- Child Composables for Settings Screen ---

@Composable
fun SettingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun ThemeSelector(selectedTheme: String, onThemeSelected: (String) -> Unit) {
    val themes = listOf("Claro", "Oscuro", "Sistema")
    Row(modifier = Modifier.fillMaxWidth()) {
        themes.forEach { theme ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onThemeSelected(theme) }
            ) {
                RadioButton(
                    selected = selectedTheme == theme,
                    onClick = { onThemeSelected(theme) }
                )
                Text(text = theme, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

@Composable
fun SettingItem(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Borrado") },
        text = { Text("¿Estás seguro de que quieres borrar todos los gastos? Esta acción no se puede deshacer.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Borrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CurrencySelectionDialog(onCurrencySelected: (String) -> Unit, onDismiss: () -> Unit) {
    val currencies = listOf("USD ($)", "EUR (€)", "MXN ($)", "GBP (£)", "JPY (¥)")
    Dialog(onDismissRequest = onDismiss) {
        Card {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Text("Seleccionar Moneda", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
                }
                items(currencies) { currency ->
                    Text(
                        text = currency,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCurrencySelected(currency) }
                            .padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MonthlyLimitDialog(initialValue: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Establecer Límite Mensual") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    if (newText.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) {
                        text = newText
                    }
                },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(onClick = { onSave(text) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
