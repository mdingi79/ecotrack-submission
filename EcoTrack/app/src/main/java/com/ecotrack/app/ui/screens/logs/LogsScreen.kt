package com.ecotrack.app.ui.screens.logs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecotrack.app.ui.components.ImpactBadge
import com.ecotrack.app.viewmodel.CategoriesViewModel
import com.ecotrack.app.viewmodel.LogsViewModel
import com.ecotrack.app.viewmodel.ViewModelFactory
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(factory: ViewModelFactory) {
    val viewModel: LogsViewModel = viewModel(factory = factory)
    val categoriesViewModel: CategoriesViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val categoriesState by categoriesViewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.formSubmitted) {
        if (uiState.formSubmitted) {
            showAddSheet = false
            viewModel.consumeFormSubmitted()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Waste Logs") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Log new waste item")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            if (uiState.logs.isEmpty() && !uiState.isLoading) {
                Text("No waste logged yet. Tap + to log your first item.")
            }
            LazyColumn {
                items(uiState.logs, key = { it.id }) { log ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(log.itemType, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                Text("${log.weightKg} kg Â· ${log.categoryName}", style = MaterialTheme.typography.bodyMedium)
                            }
                            ImpactBadge(log.impactScore, modifier = Modifier.padding(end = 8.dp))
                            IconButton(onClick = { viewModel.deleteLog(log.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete log")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        ModalBottomSheet(onDismissRequest = { showAddSheet = false }) {
            AddLogForm(
                categories = categoriesState.categories,
                errorMessage = uiState.errorMessage,
                isSaving = uiState.isLoading,
                onSave = { itemType, weight, categoryId, daysUntilCollection ->
                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    calendar.add(Calendar.DAY_OF_YEAR, daysUntilCollection)
                    val isoDate = java.time.Instant.ofEpochMilli(calendar.timeInMillis).toString()
                    viewModel.addLog(itemType, weight, categoryId, isoDate)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddLogForm(
    categories: List<com.ecotrack.app.data.local.WasteCategoryEntity>,
    errorMessage: String?,
    isSaving: Boolean,
    onSave: (itemType: String, weightText: String, categoryId: String?, daysUntilCollection: Int) -> Unit
) {
    var itemType by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id) }
    var daysUntilCollection by remember { mutableStateOf(3) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Log a waste item", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = itemType, onValueChange = { itemType = it },
            label = { Text("Item type (e.g. Plastic bottles)") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = weight, onValueChange = { weight = it },
            label = { Text("Weight (kg)") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(expanded = categoryMenuExpanded, onExpandedChange = { categoryMenuExpanded = it }) {
            OutlinedTextField(
                value = categories.find { it.id == selectedCategoryId }?.categoryName ?: "Select a category",
                onValueChange = {}, readOnly = true,
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = categoryMenuExpanded, onDismissRequest = { categoryMenuExpanded = false }) {
                if (categories.isEmpty()) {
                    DropdownMenuItem(text = { Text("Add a category first, in the Categories tab") }, onClick = {})
                }
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.categoryName) },
                        onClick = { selectedCategoryId = category.id; categoryMenuExpanded = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Collection / drop-off in $daysUntilCollection day(s)", style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = daysUntilCollection.toFloat(),
            onValueChange = { daysUntilCollection = it.toInt() },
            valueRange = 0f..14f,
            steps = 13
        )

        if (errorMessage != null) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSave(itemType, weight, selectedCategoryId, daysUntilCollection) },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSaving) CircularProgressIndicator(modifier = Modifier.size(18.dp)) else Text("Save log")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

