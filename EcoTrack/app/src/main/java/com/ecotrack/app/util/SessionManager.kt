package com.ecotrack.app.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "ecotrack_prefs")

/**
 * Stores the JWT (never the password) and lightweight UI prefs (theme, language, notification
 * toggle) locally on the device via Jetpack DataStore.
 */
class SessionManager(private val context: Context) {

    private object Keys {
        val TOKEN = stringPreferencesKey("auth_token")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val LANGUAGE = stringPreferencesKey("language") // "en" | "zu" | "xh"
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[Keys.TOKEN] }
    val darkThemeFlow: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK_THEME] ?: false }
    val languageFlow: Flow<String> = context.dataStore.data.map { it[Keys.LANGUAGE] ?: "en" }
    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[Keys.NOTIFICATIONS_ENABLED] ?: true }
    val userNameFlow: Flow<String?> = context.dataStore.data.map { it[Keys.USER_NAME] }
    val userEmailFlow: Flow<String?> = context.dataStore.data.map { it[Keys.USER_EMAIL] }

    suspend fun saveSession(token: String, name: String, email: String) {
        context.dataStore.edit {
            it[Keys.TOKEN] = token
            it[Keys.USER_NAME] = name
            it[Keys.USER_EMAIL] = email
        }
    }

    suspend fun getTokenOnce(): String? = tokenFlow.first()

    suspend fun clearSession() {
        context.dataStore.edit {
            it.remove(Keys.TOKEN)
            it.remove(Keys.USER_NAME)
            it.remove(Keys.USER_EMAIL)
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    suspend fun setLanguage(code: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = code }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }
}
