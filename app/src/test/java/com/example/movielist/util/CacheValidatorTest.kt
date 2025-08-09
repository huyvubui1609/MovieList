package com.example.movielist.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CacheValidatorTest {

    @Test
    fun `isValid returns true for fresh cache within 24 hours`() {
        val now = System.currentTimeMillis()
        val twentyThreeHoursAgo = now - (23 * 60 * 60 * 1000L)

        assertTrue(CacheValidator.isValid(twentyThreeHoursAgo))
    }

    @Test
    fun `isValid returns false for stale cache older than 24 hours`() {
        val now = System.currentTimeMillis()
        val twentyFiveHoursAgo = now - (25 * 60 * 60 * 1000L)

        assertFalse(CacheValidator.isValid(twentyFiveHoursAgo))
    }

    @Test
    fun `isValid returns true for cache exactly at boundary`() {
        val now = System.currentTimeMillis()
        val exactlyTwentyFourHoursAgo = now - (24 * 60 * 60 * 1000L)

        // Should be false as it's exactly at the boundary (not less than)
        assertFalse(CacheValidator.isValid(exactlyTwentyFourHoursAgo))
    }

    @Test
    fun `isValid works with custom max age`() {
        val now = System.currentTimeMillis()
        val twoHoursAgo = now - (2 * 60 * 60 * 1000L)
        val oneHourInMillis = 60 * 60 * 1000L

        assertFalse(CacheValidator.isValid(twoHoursAgo, oneHourInMillis))
    }

    @Test
    fun `isValid returns true for very recent cache`() {
        val now = System.currentTimeMillis()
        val oneMinuteAgo = now - (60 * 1000L)

        assertTrue(CacheValidator.isValid(oneMinuteAgo))
    }
}
