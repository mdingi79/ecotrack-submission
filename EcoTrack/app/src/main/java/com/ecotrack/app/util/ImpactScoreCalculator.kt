package com.ecotrack.app.util

import java.util.Date
import java.util.concurrent.TimeUnit

enum class MaterialType { HAZARDOUS, RECYCLABLE, ORGANIC }
enum class ImpactScore { HIGH, MEDIUM, LOW }

/**
 * Client-side mirror of the backend's Impact Score engine (utils/impactScore.js) so the UI can
 * show a score immediately while the request is in flight, and so logic can be unit tested here.
 *
 * Rules from the Part 1 design doc:
 *  - High:   hazardous material OR collection is less than 24 hours away
 *  - Medium: recyclable material OR collection is less than 3 days away
 *  - Low:    organic / non-urgent material with a drop-off more than 7 days away
 */
object ImpactScoreCalculator {

    fun calculate(materialType: MaterialType, now: Date, collectionDate: Date): ImpactScore {
        val hoursUntilCollection = TimeUnit.MILLISECONDS.toMinutes(
            collectionDate.time - now.time
        ) / 60.0
        val daysUntilCollection = hoursUntilCollection / 24.0

        return when {
            materialType == MaterialType.HAZARDOUS || hoursUntilCollection < 24 -> ImpactScore.HIGH
            materialType == MaterialType.RECYCLABLE || daysUntilCollection < 3 -> ImpactScore.MEDIUM
            else -> ImpactScore.LOW
        }
    }
}
