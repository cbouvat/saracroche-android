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
    private val ALLOW_ONLY_CONTACTS_CALLS_KEY = booleanPreferencesKey("allow_only_contacts_calls")

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
     * Get the flow of the allow only contacts setting
     */
    fun getAllowOnlyContactsFlow(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[ALLOW_ONLY_CONTACTS_CALLS_KEY] ?: false
        }
    }

    /**
     * Set the allow only contacts setting
     */
    suspend fun setAllowOnlyContacts(context: Context, allowOnlyContacts: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ALLOW_ONLY_CONTACTS_CALLS_KEY] = allowOnlyContacts
        }
    }

    /**
     * Get the current value of the allow only contacts setting (suspend function)
     */
    suspend fun isOnlyContactsAllowed(context: Context): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[ALLOW_ONLY_CONTACTS_CALLS_KEY] ?: false
        }.first()
    }
}
