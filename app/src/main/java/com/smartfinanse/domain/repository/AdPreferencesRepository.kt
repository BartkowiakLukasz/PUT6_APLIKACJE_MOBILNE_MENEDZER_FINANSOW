package com.smartfinanse.domain.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.adDataStore: DataStore<Preferences> by preferencesDataStore(name = "ad_preferences")

@Singleton
class AdPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val LAST_AD_SHOWN_DATE = stringPreferencesKey("last_ad_shown_date")

    val lastAdShownDate: Flow<String?> = context.adDataStore.data
        .map { preferences ->
            preferences[LAST_AD_SHOWN_DATE]
        }

    suspend fun setLastAdShownDate(date: String) {
        context.adDataStore.edit { preferences ->
            preferences[LAST_AD_SHOWN_DATE] = date
        }
    }
}
