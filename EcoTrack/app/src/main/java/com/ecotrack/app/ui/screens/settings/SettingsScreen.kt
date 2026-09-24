package com.ecotrack.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecotrack.app.viewmodel.SettingsViewModel
import com.ecotrack.app.viewmodel.ViewModelFactory

private val LANGUAGES = listOf("en" to "English", "zu" to "isiZulu", "xh" to "isiXhosa")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(factory: ViewModelFactory, onLoggedOut: () -> Unit) {
    val viewModel: SettingsViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {

            SectionLabel("Profile")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(uiState.userName.ifBlank { "—" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(uiState.userEmail.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionLabel("Preferences")

            SettingsRow(label = "Dark theme") {
                Switch(checked = uiState.darkTheme, onCheckedChange = { viewModel.setDarkTheme(it) })
            }

            SettingsRow(label = "Notifications") {
                Switch(checked = uiState.notificationsEnabled, onCheckedChange = { viewModel.setNotificationsEnabled(it) })
            }

            SettingsRow(label = "Language") {
                Box {
                    TextButton(onClick = { languageMenuExpanded = true }) {
                        Text(LANGUAGES.find { it.first == uiState.language }?.second ?: "English")
                    }
                    DropdownMenu(expanded = languageMenuExpanded, onDismissRequest = { languageMenuExpanded = false }) {
                        LANGUAGES.forEach { (code, label) ->
                            DropdownMenuItem(text = { Text(label) }, onClick = {
                                viewModel.setLanguage(code)
                                languageMenuExpanded = false
                            })
                        }
                    }
                }
            }
            Text(
                "Full translations are planned for a later release - this prototype stores your choice only.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
            OutlinedButton(
                onClick = { showLogoutConfirm = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log out")
            }
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Log out?") },
            text = { Text("You'll need to log in again to see your data.") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirm = false
                    viewModel.logout()
                    onLoggedOut()
                }) { Text("Log out") }
            },
            dismissButton = { TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun SettingsRow(label: String, control: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        control()
    }
}
