package com.ecotrack.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local read-cache of the WasteCategories the server holds for this user.
 * This is a simple cache for offline viewing in the prototype - NOT the full
 * offline sync engine described in the PoE brief, which is intentionally out of scope here.
 */
@Entity(tableName = "waste_categories")
data class WasteCategoryEntity(
    @PrimaryKey val id: String,
    val categoryName: String,
    val co2Factor: Double,
    val materialType: String
)

/** Local read-cache of the current user's waste logs, mirroring the API response shape. */
@Entity(tableName = "waste_logs")
data class WasteLogEntity(
    @PrimaryKey val id: String,
    val itemType: String,
    val weightKg: Double,
    val dateLogged: String,
    val collectionDate: String,
    val impactScore: String,
    val categoryId: String,
    val categoryName: String
)
