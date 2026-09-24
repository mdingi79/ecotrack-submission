package com.ecotrack.app.data.remote

data class RegisterRequest(val name: String, val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)

data class UserDto(val id: String, val name: String, val email: String)
data class AuthResponse(val token: String, val user: UserDto)

data class CategoryDto(
    val _id: String,
    val categoryName: String,
    val co2Factor: Double,
    val materialType: String
)

data class CreateCategoryRequest(val categoryName: String, val co2Factor: Double, val materialType: String)

data class CreateLogRequest(
    val itemType: String,
    val weightKg: Double,
    val categoryId: String,
    val collectionDate: String // ISO-8601
)

data class UpdateLogRequest(
    val itemType: String?,
    val weightKg: Double?,
    val categoryId: String?,
    val collectionDate: String?
)

data class CreateLogResponse(val logId: String, val itemType: String, val weightKg: Double, val impactScore: String)

data class WasteLogDto(
    val _id: String,
    val itemType: String,
    val weightKg: Double,
    val dateLogged: String,
    val collectionDate: String,
    val impactScore: String,
    val categoryId: CategoryDto?
)

data class UpcomingCollectionDto(val logId: String, val itemType: String, val collectionDate: String, val impactScore: String)

data class DashboardSummaryDto(
    val totalWeightKg: Double,
    val totalCo2SavedKg: Double,
    val upcomingCollections: List<UpcomingCollectionDto>
)

data class ApiErrorBody(val error: String)
