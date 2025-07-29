package com.cbouvat.android.saracroche.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import kotlin.math.pow

/**
 * Data class representing a blocked prefix pattern
 */
data class BlockedPrefix(
    val operator: String,
    val prefix: String,
    val pattern: String
)

/**
 * Manager for handling blocked prefixes from JSON file
 */
object BlockedPrefixManager {

    private var cachedPrefixes: List<BlockedPrefix>? = null

    /**
     * Get list of blocked prefixes from JSON file
     */
    fun getBlockedPrefixes(context: Context): List<BlockedPrefix> {
        if (cachedPrefixes != null) {
            return cachedPrefixes!!
        }

        return try {
            val inputStream = context.assets.open("blocked-prefixes.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<BlockedPrefix>>() {}.type
            val prefixes: List<BlockedPrefix> = Gson().fromJson(reader, type)
            cachedPrefixes = prefixes
            reader.close()
            prefixes
        } catch (e: Exception) {
            // Return empty list if file cannot be read
            emptyList()
        }
    }

    /**
     * Clear cached prefixes (useful for testing or manual refresh)
     */
    fun clearCache() {
        cachedPrefixes = null
    }

    /**
     * Calculate total number of blocked numbers from patterns
     */
    fun calculateTotalBlockedNumbers(context: Context): Long {
        var totalCount = 0L

        for (prefix in getBlockedPrefixes(context)) {
            val hashCount = prefix.pattern.count { it == '#' }
            totalCount += 10.0.pow(hashCount.toDouble()).toLong()
        }

        return totalCount
    }
}
