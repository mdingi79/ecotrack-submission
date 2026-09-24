package com.ecotrack.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.app.data.repository.AuthRepository
import com.ecotrack.app.util.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = "",
    val darkTheme: Boolean = false,
    val language: String = "en",
    val notificationsEnabled: Boolean = true,
    val loggedOut: Boolean = false
)

class SettingsViewModel(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        sessionManager.userNameFlow,
        sessionManager.userEmailFlow,
        sessionManager.darkThemeFlow,
        sessionManager.languageFlow,
        sessionManager.notificationsEnabledFlow
    ) { name, email, dark, lang, notifs ->
        SettingsUiState(
            userName = name.orEmpty(),
            userEmail = email.orEmpty(),
            darkTheme = dark,
            language = lang,
            notificationsEnabled = notifs
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setDarkTheme(enabled: Boolean) = viewModelScope.launch { sessionManager.setDarkTheme(enabled) }

    // Language is a UI-only stub for this prototype stage - full localisation is a PoE-only item.
    fun setLanguage(code: String) = viewModelScope.launch { sessionManager.setLanguage(code) }

    fun setNotificationsEnabled(enabled: Boolean) = viewModelScope.launch { sessionManager.setNotificationsEnabled(enabled) }

    fun logout() = viewModelScope.launch { authRepository.logout() }
}
