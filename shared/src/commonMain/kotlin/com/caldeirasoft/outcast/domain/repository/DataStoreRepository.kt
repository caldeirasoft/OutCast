package com.caldeirasoft.outcast.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val storeCountryPreference : Flow<String>

    val lastSyncDate : Flow<Long>

    suspend fun saveStoreCountryPreference(storeRegion: String)

    suspend fun saveLastSyncDate()
}