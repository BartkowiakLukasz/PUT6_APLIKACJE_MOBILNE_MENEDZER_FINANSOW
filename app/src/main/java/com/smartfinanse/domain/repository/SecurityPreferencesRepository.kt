package com.smartfinanse.domain.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.smartfinanse.data.local.SmartFinanseDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "security_prefs")

@Singleton
class SecurityPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: SmartFinanseDatabase
) {

    private object PreferencesKeys {
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val PIN_SALT = stringPreferencesKey("pin_salt")
        val FAILED_ATTEMPTS = intPreferencesKey("failed_attempts")
        val LOCKOUT_END_TIME = longPreferencesKey("lockout_end_time")
    }

    val hasPin: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PIN_HASH] != null && preferences[PreferencesKeys.PIN_SALT] != null
    }

    val failedAttempts: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.FAILED_ATTEMPTS] ?: 0
    }

    val lockoutEndTime: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LOCKOUT_END_TIME] ?: 0L
    }

    suspend fun savePin(hash: String, salt: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PIN_HASH] = hash
            preferences[PreferencesKeys.PIN_SALT] = salt
            preferences[PreferencesKeys.FAILED_ATTEMPTS] = 0
            preferences[PreferencesKeys.LOCKOUT_END_TIME] = 0L
        }
    }

    suspend fun getPinData(): Pair<String?, String?> {
        val preferences = context.dataStore.data.first()
        return Pair(
            preferences[PreferencesKeys.PIN_HASH],
            preferences[PreferencesKeys.PIN_SALT]
        )
    }

    suspend fun incrementFailedAttempts() {
        context.dataStore.edit { preferences ->
            val currentAttempts = preferences[PreferencesKeys.FAILED_ATTEMPTS] ?: 0
            val newAttempts = currentAttempts + 1
            preferences[PreferencesKeys.FAILED_ATTEMPTS] = newAttempts

            if (newAttempts >= 5) {
                // 30 seconds lockout
                preferences[PreferencesKeys.LOCKOUT_END_TIME] = System.currentTimeMillis() + 30_000
            }
        }
    }

    suspend fun resetFailedAttempts() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FAILED_ATTEMPTS] = 0
            preferences[PreferencesKeys.LOCKOUT_END_TIME] = 0L
        }
    }

    suspend fun clearAllData() {
        withContext(Dispatchers.IO) {
            // 1. Clear DataStore
            context.dataStore.edit { it.clear() }

            // 2. Clear SharedPreferences
            context.getSharedPreferences("smart_finanse_prefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

            // 3. Clear Room Database
            database.clearAllTables()
        }
    }
}
