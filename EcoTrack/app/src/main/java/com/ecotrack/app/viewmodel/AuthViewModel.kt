package com.ecotrack.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.app.data.repository.AuthRepository
import com.ecotrack.app.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        val validationError = validateRegistration(name, email, password, confirmPassword)
        if (validationError != null) {
            _uiState.value = AuthUiState.Error(validationError)
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = repository.register(name.trim(), email.trim(), password)) {
                is ApiResult.Success -> _uiState.value = AuthUiState.Success
                is ApiResult.Failure -> _uiState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter both email and password")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = repository.login(email.trim(), password)) {
                is ApiResult.Success -> _uiState.value = AuthUiState.Success
                is ApiResult.Failure -> _uiState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun validateRegistration(name: String, email: String, password: String, confirm: String): String? {
        val emailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
        return when {
            name.isBlank() -> "Please enter your name"
            !emailRegex.matches(email.trim()) -> "Please enter a valid email address"
            password.length < 8 -> "Password must be at least 8 characters"
            password != confirm -> "Passwords do not match"
            else -> null
        }
    }
}
