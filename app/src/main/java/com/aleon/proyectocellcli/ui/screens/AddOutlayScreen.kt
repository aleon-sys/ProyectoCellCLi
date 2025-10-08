package com.aleon.proyectocellcli.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.ui.viewmodel.AddOutlayEvent
import com.aleon.proyectocellcli.ui.viewmodel.AddOutlayViewModel
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOutlayScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AddOutlayViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showLimitAlert by remember { mutableStateOf(false) }

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddOutlayEvent.SaveSuccess -> {
                    Toast.makeText(context, "¡Gasto guardado!", Toast.LENGTH_SHORT).show()
                    // If we are editing, close the screen. Otherwise, reset the form.
                    if (formState.expenseId != null) {
                        navController.popBackStack()
                    } else {
                        viewModel.resetForm()
                    }
                }
                is AddOutlayEvent.LimitExceeded -> {
                    showLimitAlert = true
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (formState.expenseId == null) "Añadir Nuevo Gasto" else "Editar Gasto",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = formState.description,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = formState.amount,
            onValueChange = {
                if (it.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                    viewModel.onAmountChange(it)
                }
            },
            label = { Text("Monto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Text(currencySymbol) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box {
            OutlinedTextField(
                value = formState.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Date Icon") },
                modifier = Modifier.fillMaxWidth()
            )
            Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
        }
        Spacer(modifier = Modifier.height(16.dp))

        CategorySelector(
            categories = categories,
            selectedCategory = formState.category,
            onCategorySelected = { viewModel.onCategoryChange(it) },
            onAddCategoryClicked = {
                categoryToEdit = null
                showCategoryDialog = true
            },
            onEditCategoryClicked = {
                categoryToEdit = it
                showCategoryDialog = true
            }
        )

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { viewModel.onSaveExpense() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = formState.category != null
        ) {
            Text("Guardar Gasto")
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = formState.date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        viewModel.onDateChange(newDate)
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showCategoryDialog) {
        CategoryEditDialog(
            category = categoryToEdit,
            onDismiss = { showCategoryDialog = false },
            onSave = {
                if (categoryToEdit == null) {
                    viewModel.onAddCategory(it.name, it.color)
                } else {
                    viewModel.onUpdateCategory(it)
                }
                showCategoryDialog = false
            }
        )
    }

    if (showLimitAlert) {
        AlertDialog(
            onDismissRequest = { showLimitAlert = false },
            title = { Text("Límite Excedido") },
            text = { Text("Has superado tu límite de gastos mensual. El gasto se guardará de todas formas.") },
            confirmButton = { Button(onClick = { showLimitAlert = false }) { Text("Entendido") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelector(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    onAddCategoryClicked: () -> Unit,
    onEditCategoryClicked: (Category) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        if (categories.isNotEmpty() && selectedCategory != null) {
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedCategory.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    leadingIcon = { Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(selectedCategory.color)) },
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
                                onCategorySelected(category)
                                isDropdownExpanded = false
                            },
                            trailingIcon = {
                                Row {
                                    IconButton(onClick = { onEditCategoryClicked(category) }) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar categoría")
                                    }
                                    if (categories.size > 1) {
                                        IconButton(onClick = { /* TODO */ }) {
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
        IconButton(onClick = onAddCategoryClicked) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir categoría")
        }
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
