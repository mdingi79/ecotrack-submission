package com.ecotrack.app.util

import android.util.Log
import com.ecotrack.app.data.remote.ApiErrorBody
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

private const val TAG = "EcoTrackApiCall"

/** Wraps a network call outcome so every screen handles success/failure the same way. */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Failure(val message: String) : ApiResult<Nothing>()
}

/**
 * Runs [call], turning HTTP error bodies, network failures (no signal, DNS, timeouts) and
 * unexpected exceptions into a friendly [ApiResult.Failure] instead of letting the app crash.
 */
suspend fun <T> safeApiCall(call: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) ApiResult.Success(body) else ApiResult.Success(Unit as T)
        } else {
            val errorMessage = try {
                response.errorBody()?.string()?.let { Gson().fromJson(it, ApiErrorBody::class.java).error }
            } catch (e: Exception) {
                null
            } ?: "Request failed (${response.code()})"
            Log.w(TAG, "API error: $errorMessage")
            ApiResult.Failure(errorMessage)
        }
    } catch (e: IOException) {
        Log.e(TAG, "Network error", e)
        ApiResult.Failure("Couldn't reach the server. Check your internet connection and try again.")
    } catch (e: Exception) {
        Log.e(TAG, "Unexpected error during API call", e)
        ApiResult.Failure("Something went wrong. Please try again.")
    }
}
