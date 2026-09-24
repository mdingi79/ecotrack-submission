package com.ecotrack.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EcoTrackApiService {

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @GET("api/categories")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @POST("api/categories")
    suspend fun createCategory(@Body body: CreateCategoryRequest): Response<CategoryDto>

    @POST("api/logs")
    suspend fun createLog(@Body body: CreateLogRequest): Response<CreateLogResponse>

    @GET("api/logs")
    suspend fun getLogs(): Response<List<WasteLogDto>>

    @PUT("api/logs/{id}")
    suspend fun updateLog(@Path("id") id: String, @Body body: UpdateLogRequest): Response<WasteLogDto>

    @DELETE("api/logs/{id}")
    suspend fun deleteLog(@Path("id") id: String): Response<Unit>

    @GET("api/logs/summary/dashboard")
    suspend fun getDashboardSummary(): Response<DashboardSummaryDto>
}
