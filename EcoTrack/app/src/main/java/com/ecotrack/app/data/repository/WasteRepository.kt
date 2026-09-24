package com.ecotrack.app.data.repository

import com.ecotrack.app.data.local.WasteCategoryDao
import com.ecotrack.app.data.local.WasteCategoryEntity
import com.ecotrack.app.data.local.WasteLogDao
import com.ecotrack.app.data.local.WasteLogEntity
import com.ecotrack.app.data.remote.CreateCategoryRequest
import com.ecotrack.app.data.remote.CreateLogRequest
import com.ecotrack.app.data.remote.CreateLogResponse
import com.ecotrack.app.data.remote.DashboardSummaryDto
import com.ecotrack.app.data.remote.EcoTrackApiService
import com.ecotrack.app.data.remote.UpdateLogRequest
import com.ecotrack.app.util.ApiResult
import com.ecotrack.app.util.safeApiCall
import kotlinx.coroutines.flow.Flow

class WasteRepository(
    private val api: EcoTrackApiService,
    private val categoryDao: WasteCategoryDao,
    private val logDao: WasteLogDao
) {
    fun observeCachedCategories(): Flow<List<WasteCategoryEntity>> = categoryDao.observeAll()
    fun observeCachedLogs(): Flow<List<WasteLogEntity>> = logDao.observeAll()

    suspend fun refreshCategories(): ApiResult<Unit> {
        val result = safeApiCall { api.getCategories() }
        if (result is ApiResult.Success) {
            categoryDao.upsertAll(result.data.map {
                WasteCategoryEntity(it._id, it.categoryName, it.co2Factor, it.materialType)
            })
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.Failure -> result
        }
    }

    suspend fun createCategory(name: String, co2Factor: Double, materialType: String): ApiResult<Unit> {
        val result = safeApiCall { api.createCategory(CreateCategoryRequest(name, co2Factor, materialType)) }
        if (result is ApiResult.Success) refreshCategories()
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.Failure -> result
        }
    }

    suspend fun refreshLogs(): ApiResult<Unit> {
        val result = safeApiCall { api.getLogs() }
        if (result is ApiResult.Success) {
            logDao.upsertAll(result.data.map { log ->
                WasteLogEntity(
                    id = log._id,
                    itemType = log.itemType,
                    weightKg = log.weightKg,
                    dateLogged = log.dateLogged,
                    collectionDate = log.collectionDate,
                    impactScore = log.impactScore,
                    categoryId = log.categoryId?._id.orEmpty(),
                    categoryName = log.categoryId?.categoryName.orEmpty()
                )
            })
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.Failure -> result
        }
    }

    suspend fun createLog(
        itemType: String,
        weightKg: Double,
        categoryId: String,
        collectionDateIso: String
    ): ApiResult<CreateLogResponse> {
        val result = safeApiCall {
            api.createLog(CreateLogRequest(itemType, weightKg, categoryId, collectionDateIso))
        }
        if (result is ApiResult.Success) refreshLogs()
        return result
    }

    suspend fun updateLog(
        logId: String,
        itemType: String?,
        weightKg: Double?,
        categoryId: String?,
        collectionDateIso: String?
    ): ApiResult<Unit> {
        val result = safeApiCall {
            api.updateLog(logId, UpdateLogRequest(itemType, weightKg, categoryId, collectionDateIso))
        }
        if (result is ApiResult.Success) refreshLogs()
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.Failure -> result
        }
    }

    suspend fun deleteLog(logId: String): ApiResult<Unit> {
        val result = safeApiCall { api.deleteLog(logId) }
        if (result is ApiResult.Success) logDao.deleteById(logId)
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(Unit)
            is ApiResult.Failure -> result
        }
    }

    suspend fun getDashboardSummary(): ApiResult<DashboardSummaryDto> =
        safeApiCall { api.getDashboardSummary() }
}
