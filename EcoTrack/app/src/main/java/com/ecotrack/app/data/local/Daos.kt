package com.ecotrack.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WasteCategoryDao {
    @Query("SELECT * FROM waste_categories ORDER BY categoryName ASC")
    fun observeAll(): Flow<List<WasteCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(categories: List<WasteCategoryEntity>)

    @Query("DELETE FROM waste_categories")
    suspend fun clear()
}

@Dao
interface WasteLogDao {
    @Query("SELECT * FROM waste_logs ORDER BY dateLogged DESC")
    fun observeAll(): Flow<List<WasteLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(logs: List<WasteLogEntity>)

    @Query("DELETE FROM waste_logs WHERE id = :logId")
    suspend fun deleteById(logId: String)

    @Query("DELETE FROM waste_logs")
    suspend fun clear()
}
