package com.ecotrack.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ecotrack.app.EcoTrackApp

/** Simple factory that wires each ViewModel to the repositories/session manager it needs. */
class ViewModelFactory(private val app: EcoTrackApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(app.authRepository) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(app.wasteRepository) as T
            modelClass.isAssignableFrom(LogsViewModel::class.java) ->
                LogsViewModel(app.wasteRepository) as T
            modelClass.isAssignableFrom(CategoriesViewModel::class.java) ->
                CategoriesViewModel(app.wasteRepository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(app.sessionManager, app.authRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
