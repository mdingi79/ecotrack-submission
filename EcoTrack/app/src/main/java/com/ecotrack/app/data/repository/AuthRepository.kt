package com.ecotrack.app.data.repository

import com.ecotrack.app.data.remote.EcoTrackApiService
import com.ecotrack.app.data.remote.LoginRequest
import com.ecotrack.app.data.remote.RegisterRequest
import com.ecotrack.app.util.ApiResult
import com.ecotrack.app.util.SessionManager
import com.ecotrack.app.util.safeApiCall

class AuthRepository(
    private val api: EcoTrackApiService,
    private val sessionManager: SessionManager
) {
    suspend fun register(name: String, email: String, password: String): ApiResult<Unit> {
        val result = safeApiCall { api.register(RegisterRequest(name, email, password)) }
        if (result is ApiResult.Success) {
            sessionManager.saveSession(result.data.token, result.data.user.name, result.data.user.email)
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.Failure -> result
        }
    }

    suspend fun login(email: String, password: String): ApiResult<Unit> {
        val result = safeApiCall { api.login(LoginRequest(email, password)) }
        if (result is ApiResult.Success) {
            sessionManager.saveSession(result.data.token, result.data.user.name, result.data.user.email)
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.Failure -> result
        }
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    suspend fun isLoggedIn(): Boolean = !sessionManager.getTokenOnce().isNullOrBlank()
}
