package com.example.movielist.util

object CacheValidator {
    private const val DEFAULT_MAX_AGE_MS = 24 * 60 * 60 * 1000L

    fun isValid(lastFetched: Long, maxAgeMillis: Long = DEFAULT_MAX_AGE_MS): Boolean {
        return System.currentTimeMillis() - lastFetched < maxAgeMillis
    }
}
