package com.ecotrack.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.app.data.local.WasteLogEntity
import com.ecotrack.app.data.repository.WasteRepository
import com.ecotrack.app.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LogsUiState(
    val logs: List<WasteLogEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val formSubmitted: Boolean = false
)

class LogsViewModel(private val repository: WasteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LogsUiState())
    val uiState: StateFlow<LogsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeCachedLogs().collect { cached ->
                _uiState.value = _uiState.value.copy(logs = cached)
            }
        }
        refresh()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.refreshLogs()) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(isLoading = false)
                is ApiResult.Failure -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
            }
        }
    }

    /** Validates the add-log form before touching the network, so bad input never crashes the app. */
    fun addLog(itemType: String, weightKgText: String, categoryId: String?, collectionDateIso: String?) {
        val weightKg = weightKgText.toDoubleOrNull()
        val validationError = when {
            itemType.isBlank() -> "Please enter what kind of item this is"
            weightKg == null || weightKg <= 0.0 -> "Please enter a valid weight greater than 0"
            categoryId.isNullOrBlank() -> "Please choose a category"
            collectionDateIso.isNullOrBlank() -> "Please choose a collection date"
            else -> null
        }
        if (validationError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = validationError, formSubmitted = false)
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.createLog(itemType.trim(), weightKg!!, categoryId!!, collectionDateIso!!)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(isLoading = false, formSubmitted = true)
                is ApiResult.Failure -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
            }
        }
    }

    fun deleteLog(logId: String) {
        viewModelScope.launch {
            when (val result = repository.deleteLog(logId)) {
                is ApiResult.Success -> Unit
                is ApiResult.Failure -> _uiState.value = _uiState.value.copy(errorMessage = result.message)
            }
        }
    }

    fun consumeFormSubmitted() {
        _uiState.value = _uiState.value.copy(formSubmitted = false)
    }
}
