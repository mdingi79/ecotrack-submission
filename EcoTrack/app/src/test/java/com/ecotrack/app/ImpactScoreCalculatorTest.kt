package com.ecotrack.app

import com.ecotrack.app.util.ImpactScore
import com.ecotrack.app.util.ImpactScoreCalculator
import com.ecotrack.app.util.MaterialType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class ImpactScoreCalculatorTest {

    private val now = Date(1790000000000L) // fixed reference instant

    private fun daysFromNow(days: Long): Date = Date(now.time + days * 24 * 60 * 60 * 1000)
    private fun hoursFromNow(hours: Long): Date = Date(now.time + hours * 60 * 60 * 1000)

    @Test
    fun `hazardous material is always High regardless of collection date`() {
        val result = ImpactScoreCalculator.calculate(MaterialType.HAZARDOUS, now, daysFromNow(20))
        assertEquals(ImpactScore.HIGH, result)
    }

    @Test
    fun `collection under 24 hours away is High even for organic material`() {
        val result = ImpactScoreCalculator.calculate(MaterialType.ORGANIC, now, hoursFromNow(10))
        assertEquals(ImpactScore.HIGH, result)
    }

    @Test
    fun `recyclable material is Medium when more than a day but within the week`() {
        val result = ImpactScoreCalculator.calculate(MaterialType.RECYCLABLE, now, daysFromNow(5))
        assertEquals(ImpactScore.MEDIUM, result)
    }

    @Test
    fun `organic material under 3 days away is Medium`() {
        val result = ImpactScoreCalculator.calculate(MaterialType.ORGANIC, now, daysFromNow(2))
        assertEquals(ImpactScore.MEDIUM, result)
    }

    @Test
    fun `organic material more than 7 days away is Low`() {
        val result = ImpactScoreCalculator.calculate(MaterialType.ORGANIC, now, daysFromNow(10))
        assertEquals(ImpactScore.LOW, result)
    }
}
