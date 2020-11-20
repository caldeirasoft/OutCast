package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.createDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.*
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor (@ApplicationContext val context: Context)
    : DataStoreRepository {
    companion object {
        const val DATA_STORE_FILE_NAME = "user_prefs"
    }

    private object PreferenceKeys {
        val STORE_REGION = preferencesKey<String>("store_country")
        val LAST_SYNC = preferencesKey<Long>("last_sync")
    }

    // Build the DataStore
    private val dataStore = context.createDataStore(name = DATA_STORE_FILE_NAME)

    private val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException)
                emit(emptyPreferences())
            else throw exception
        }

    override val storeCountryPreference: Flow<String>
        = preferencesFlow
        .map { preferences -> preferences[PreferenceKeys.STORE_REGION] ?: "en-US" }

    override suspend fun saveStoreCountryPreference(storeRegion: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.STORE_REGION] = storeRegion
        }
    }

    override val lastSyncDate: Flow<Long>
            = preferencesFlow
        .map { preferences -> preferences[PreferenceKeys.LAST_SYNC] ?: -1 }

    override suspend fun saveLastSyncDate() {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_SYNC] = Calendar.getInstance().timeInMillis
        }
    }

}