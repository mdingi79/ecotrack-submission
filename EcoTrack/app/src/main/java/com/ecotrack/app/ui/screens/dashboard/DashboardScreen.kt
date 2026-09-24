package com.ecotrack.app.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecotrack.app.ui.components.ImpactBadge
import com.ecotrack.app.viewmodel.DashboardViewModel
import com.ecotrack.app.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(factory: ViewModelFactory) {
    val viewModel: DashboardViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Impact") },
                actions = {
                    IconButton(onClick = { viewModel.loadSummary() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh dashboard")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                uiState.isLoading && uiState.summary == null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.errorMessage != null && uiState.summary == null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(uiState.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                    }
                }
                uiState.summary != null -> {
                    val summary = uiState.summary!!

                    Row(modifier = Modifier.fillMaxWidth()) {
                        StatCard(
                            title = "Weight recycled",
                            value = "${summary.totalWeightKg} kg",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        StatCard(
                            title = "CO2 saved (est.)",
                            value = "${summary.totalCo2SavedKg} kg",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Upcoming collections", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (summary.upcomingCollections.isEmpty()) {
                        Text(
                            "Nothing scheduled - log a waste item with a collection date to see it here.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        LazyColumn {
                            items(summary.upcomingCollections) { collection ->
                                val dateFormat = remember { SimpleDateFormat("EEE, d MMM · HH:mm", Locale.getDefault()) }
                                val parsedDate = remember(collection.collectionDate) {
                                    runCatching {
                                        java.time.Instant.parse(collection.collectionDate)
                                    }.getOrNull()
                                }
                                Card(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(collection.itemType, style = MaterialTheme.typography.bodyLarge)
                                            Text(
                                                parsedDate?.let { dateFormat.format(java.util.Date.from(it)) } ?: collection.collectionDate,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        ImpactBadge(collection.impactScore)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}
