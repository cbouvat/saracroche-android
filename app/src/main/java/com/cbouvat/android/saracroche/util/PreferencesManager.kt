package com.cbouvat.android.saracroche.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extension to create DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

/**
 * Manager for app preferences using DataStore
 */
object PreferencesManager {

    private val BLOCK_ANONYMOUS_CALLS_KEY = booleanPreferencesKey("block_anonymous_calls")
    private val BLOCK_UNKNOWN_NUMBERS_KEY = booleanPreferencesKey("block_unknown_numbers")

    /**
     * Get the flow of block anonymous calls setting
     */
    fun getBlockAnonymousCallsFlow(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[BLOCK_ANONYMOUS_CALLS_KEY] ?: false
        }
    }

    /**
     * Set the block anonymous calls setting
     */
    suspend fun setBlockAnonymousCalls(context: Context, blockAnonymous: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BLOCK_ANONYMOUS_CALLS_KEY] = blockAnonymous
        }
    }

    /**
     * Get the current value of block anonymous calls setting (suspend function)
     */
    suspend fun getBlockAnonymousCalls(context: Context): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[BLOCK_ANONYMOUS_CALLS_KEY] ?: false
        }.first()
    }

    /**
     * Get the flow of block unknown numbers setting
     */
    fun getBlockUnknownNumbersFlow(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[BLOCK_UNKNOWN_NUMBERS_KEY] ?: false
        }
    }

    /**
     * Set the block unknown numbers setting
     */
    suspend fun setBlockUnknownNumbers(context: Context, blockUnknown: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BLOCK_UNKNOWN_NUMBERS_KEY] = blockUnknown
        }
    }

    /**
     * Get the current value of block unknown numbers setting (suspend function)
     */
    suspend fun getBlockUnknownNumbers(context: Context): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[BLOCK_UNKNOWN_NUMBERS_KEY] ?: false
        }.first()
    }
}
