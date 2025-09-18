package com.cbouvat.android.saracroche.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import kotlin.math.pow

/**
 * Data class representing a blocked number pattern
 */
data class BlockedPattern(
    val operator: String,
    val pattern: String
)

/**
 * Manager for handling blocked number patterns from JSON file
 */
object BlockedPatternManager {

    private var cachedPatterns: List<BlockedPattern>? = null

    /**
     * Get list of blocked patterns from JSON file
     */
    fun getBlockedPatterns(context: Context): List<BlockedPattern> {
        if (cachedPatterns != null) {
            return cachedPatterns!!
        }

        return try {
            val inputStream = context.assets.open("blocked-patterns.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<BlockedPattern>>() {}.type
            val patterns: List<BlockedPattern> = Gson().fromJson(reader, type)
            cachedPatterns = patterns
            reader.close()
            patterns
        } catch (e: Exception) {
            // Return empty list if file cannot be read
            emptyList()
        }
    }

    /**
     * Clear cached patterns (useful for testing or manual refresh)
     */
    fun clearCache() {
        cachedPatterns = null
    }

    /**
     * Calculate total number of blocked numbers from patterns
     */
    fun calculateTotalBlockedNumbers(context: Context): Long {
        var totalCount = 0L

        for (pattern in getBlockedPatterns(context)) {
            val hashCount = pattern.pattern.count { it == '#' }
            totalCount += 10.0.pow(hashCount.toDouble()).toLong()
        }

        return totalCount
    }
}
