package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.data.repository.LocalCacheRepository.PreferenceKeys.DIRECTORY
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@ExperimentalSettingsApi
class LocalCacheRepository(
    val settings: FlowSettings,
    val json: Json
) {
    private object PreferenceKeys {
        val DIRECTORY = "storeDirectory"
    }


    val storeDirectory: Flow<StoreGroupingPage?> = settings.getStringOrNullFlow(DIRECTORY)
        .map {
            it?.let {
                val storeData: StoreGroupingPage = json.decodeFromString(serializer(), it)
                storeData
            }
        }

    suspend fun saveDirectory(storeData: StoreGroupingPage) {
        json.encodeToString(serializer(), storeData).let { jsonData ->
            settings.putString(DIRECTORY, jsonData)
        }
    }
}