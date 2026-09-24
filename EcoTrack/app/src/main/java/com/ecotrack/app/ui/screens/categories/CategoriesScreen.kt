package com.ecotrack.app.ui.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecotrack.app.viewmodel.CategoriesViewModel
import com.ecotrack.app.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(factory: ViewModelFactory) {
    val viewModel: CategoriesViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Waste Categories") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add category")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            }
            if (uiState.categories.isEmpty() && !uiState.isLoading) {
                Text("No categories yet. Tap + to add one (e.g. Plastic, E-waste, Organic).")
            }
            LazyColumn {
                items(uiState.categories) { category ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(category.categoryName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text(
                                "${category.materialType.replaceFirstChar { it.uppercase() }} · ${category.co2Factor} kg CO2 saved per kg",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, factor, material ->
                viewModel.addCategory(name, factor, material)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddCategoryDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var co2Factor by remember { mutableStateOf("") }
    var materialType by remember { mutableStateOf("recyclable") }
    val materialOptions = listOf("hazardous", "recyclable", "organic")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add category") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Category name") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = co2Factor, onValueChange = { co2Factor = it },
                    label = { Text("CO2 factor (kg saved per kg)") }, singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Material type", style = MaterialTheme.typography.labelLarge)
                materialOptions.forEach { option ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(selected = materialType == option, onClick = { materialType = option })
                        Text(option.replaceFirstChar { it.uppercase() })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, co2Factor, materialType) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
