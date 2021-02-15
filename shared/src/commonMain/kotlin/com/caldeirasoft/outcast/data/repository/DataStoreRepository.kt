package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.domain.dto.StoreFrontDto
import com.caldeirasoft.outcast.domain.util.Resources
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

abstract class DataStoreRepository(val settings: FlowSettings) {
    private object PreferenceKeys {
        val STOREFRONT_REGION = "store_front"
        val LAST_SYNC = "last_sync"
    }
    abstract val storeCountry: Flow<String>
    abstract val lastSyncDate: Flow<Long>

    //context.resources.configuration.locales.get(0).country

    suspend fun saveStoreCountryPreference(country: String) {
        settings.putString(PreferenceKeys.STOREFRONT_REGION, country)
    }

    abstract suspend fun saveLastSyncDate()

    abstract fun getCurrentStoreFront(country: String): String

    /**
     * getStoreFront
     */
    protected fun getStoreFronts(): StoreFrontDto {
        val text = Resources.getResourceContent("store_fronts.json")
        val nonStrictJson = Json { isLenient = true; ignoreUnknownKeys = true }
        return nonStrictJson.decodeFromString(StoreFrontDto.serializer(), text)
    }
}