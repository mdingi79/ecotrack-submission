package com.ecotrack.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.app.data.local.WasteCategoryEntity
import com.ecotrack.app.data.repository.WasteRepository
import com.ecotrack.app.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoriesUiState(
    val categories: List<WasteCategoryEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CategoriesViewModel(private val repository: WasteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeCachedCategories().collect { cached ->
                _uiState.value = _uiState.value.copy(categories = cached)
            }
        }
        refresh()
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.refreshCategories()) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(isLoading = false)
                is ApiResult.Failure -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
            }
        }
    }

    fun addCategory(name: String, co2FactorText: String, materialType: String) {
        val co2Factor = co2FactorText.toDoubleOrNull()
        val validationError = when {
            name.isBlank() -> "Please enter a category name"
            co2Factor == null || co2Factor < 0.0 -> "Please enter a valid CO2 factor (kg saved per kg recycled)"
            else -> null
        }
        if (validationError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = validationError)
            return
        }

        viewModelScope.launch {
            when (val result = repository.createCategory(name.trim(), co2Factor!!, materialType)) {
                is ApiResult.Success -> Unit
                is ApiResult.Failure -> _uiState.value = _uiState.value.copy(errorMessage = result.message)
            }
        }
    }
}
