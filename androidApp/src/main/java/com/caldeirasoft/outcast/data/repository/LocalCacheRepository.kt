package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.IOException

class LocalCacheRepository(
    val context: Context,
    val json: Json) {
    companion object {
        const val CACHE_STORE_FILE_NAME = "cache.db"
    }

    private object PreferenceKeys {
        val DIRECTORY = stringPreferencesKey("storeDirectory")
    }

    // Build the DataStore
    private val cacheDataStore: DataStore<Preferences> =
        context.createDataStore(name = CACHE_STORE_FILE_NAME)

    private val preferencesFlow = cacheDataStore.data
        .catch { exception ->
            if (exception is IOException)
                emit(emptyPreferences())
            else throw exception
        }

    val storeDirectory: Flow<StoreGroupingPage?>
            = preferencesFlow
        .map { preferences -> preferences[PreferenceKeys.DIRECTORY] }
        .map {
            it?.let {
                val storeData: StoreGroupingPage = json.decodeFromString(serializer(), it)
                storeData
            }
        }

    suspend fun saveDirectory(storeData: StoreGroupingPage) {
        cacheDataStore.edit { preferences ->
            preferences[PreferenceKeys.DIRECTORY] = json.encodeToString(serializer(), storeData)
        }
    }
}