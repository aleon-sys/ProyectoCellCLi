package com.aleon.proyectocellcli.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.ui.viewmodel.AddOutlayViewModel
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

import com.aleon.proyectocellcli.ui.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOutlayScreen(
    modifier: Modifier = Modifier,
    viewModel: AddOutlayViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    // --- State ---
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val categories by viewModel.categories.collectAsState()
    var selectedCategory by remember(categories) { mutableStateOf(categories.firstOrNull()) }
    
    val currency by mainViewModel.currency.collectAsState()
    val currencySymbol = remember(currency) {
        currency.substringAfter("(").substringBefore(")")
    }
    
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    // --- UI ---
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Añadir Nuevo Gasto", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { newAmount ->
                // Regex to allow only numbers and up to two decimal places
                if (newAmount.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) {
                    amount = newAmount
                }
            },
            label = { Text("Monto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Text(currencySymbol) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Date Picker Field
        Box {
            OutlinedTextField(
                value = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Date Icon") },
                modifier = Modifier.fillMaxWidth()
            )
            // Transparent box to intercept clicks
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (categories.isNotEmpty() && selectedCategory != null) {
                ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }, modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = selectedCategory!!.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        leadingIcon = { Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(selectedCategory!!.color)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(category.color))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(category.name)
                                    }
                                },
                                onClick = {
                                    selectedCategory = category
                                    isDropdownExpanded = false
                                },
                                trailingIcon = {
                                    Row {
                                        IconButton(onClick = {
                                            categoryToEdit = category
                                            showCategoryDialog = true
                                        }) {
                                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar categoría")
                                        }
                                        if (categories.size > 1) {
                                            IconButton(onClick = {
                                                // TODO: viewModel.onDeleteCategory(category)
                                            }) {
                                                Icon(imageVector = Icons.Default.Close, contentDescription = "Borrar categoría")
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                categoryToEdit = null
                showCategoryDialog = true
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir categoría")
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                selectedCategory?.let {
                    viewModel.onSaveExpense(description, amount, it, selectedDate)
                    // Clear fields after saving
                    description = ""
                    amount = ""
                    selectedDate = LocalDate.now()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = selectedCategory != null
        ) {
            Text("Guardar Gasto")
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showCategoryDialog) {
        CategoryEditDialog(
            category = categoryToEdit,
            onDismiss = { showCategoryDialog = false },
            onSave = { updatedCategory ->
                if (categoryToEdit == null) {
                    viewModel.onAddCategory(updatedCategory.name, updatedCategory.color)
                } else {
                    viewModel.onUpdateCategory(updatedCategory)
                }
                showCategoryDialog = false
            }
        )
    }
}

@Composable
fun CategoryEditDialog(category: Category?, onDismiss: () -> Unit, onSave: (Category) -> Unit) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    val controller = rememberColorPickerController()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "Añadir Categoría" else "Editar Categoría") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    controller = controller
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) {
                    val newCategory = category?.copy(name = name, color = controller.selectedColor.value)
                        ?: Category(name = name, color = controller.selectedColor.value)
                    onSave(newCategory)
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Preview(showBackground = true)
@Composable
fun AddOutlayScreenPreview() {
    // Preview won't work with Hilt ViewModel by default.
}