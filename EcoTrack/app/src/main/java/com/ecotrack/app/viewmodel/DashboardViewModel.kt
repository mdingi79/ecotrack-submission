package com.ecotrack.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecotrack.app.data.remote.DashboardSummaryDto
import com.ecotrack.app.data.repository.WasteRepository
import com.ecotrack.app.util.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val summary: DashboardSummaryDto? = null,
    val errorMessage: String? = null
)

class DashboardViewModel(private val repository: WasteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadSummary()
    }

    fun loadSummary() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = repository.getDashboardSummary()) {
                is ApiResult.Success -> _uiState.value = DashboardUiState(isLoading = false, summary = result.data)
                is ApiResult.Failure -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
            }
        }
    }
}
