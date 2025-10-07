package com.aleon.proyectocellcli.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.UUID
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

// --- Data Model ---
data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOutlayScreen(modifier: Modifier = Modifier) {
    // --- State ---
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    val categories = remember { mutableStateListOf(
        Category(name = "Comida", color = Color(0xFFFACD3D)),
        Category(name = "Transporte", color = Color(0xFF5B94E3)),
        Category(name = "Ocio", color = Color(0xFFE35B5B)),
        Category(name = "Hogar", color = Color(0xFF68C67D))
    )}
    var selectedCategory by remember { mutableStateOf(categories.first()) }
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
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Monto") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), leadingIcon = { Text("$") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }, modifier = Modifier.weight(1f)) {
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
                                            val wasSelected = selectedCategory.id == category.id
                                            categories.remove(category)
                                            if (wasSelected) {
                                                selectedCategory = categories.first()
                                            }
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
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                categoryToEdit = null // Set to null for "Add New" mode
                showCategoryDialog = true
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir categoría")
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text("Guardar Gasto")
        }
    }

    if (showCategoryDialog) {
        CategoryEditDialog(
            category = categoryToEdit,
            onDismiss = { showCategoryDialog = false },
            onSave = { updatedCategory ->
                val index = categories.indexOfFirst { it.id == updatedCategory.id }
                if (index != -1) { // Editing existing
                    categories[index] = updatedCategory
                    if (selectedCategory.id == updatedCategory.id) {
                        selectedCategory = updatedCategory
                    }
                } else { // Adding new
                    categories.add(updatedCategory)
                    selectedCategory = updatedCategory
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
    AddOutlayScreen()
}
