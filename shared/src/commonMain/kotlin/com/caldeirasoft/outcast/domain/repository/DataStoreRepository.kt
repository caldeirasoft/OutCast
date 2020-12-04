package com.caldeirasoft.outcast.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val storeCountry : Flow<String>

    val lastSyncDate : Flow<Long>

    suspend fun saveStoreCountryPreference(country: String)

    suspend fun saveLastSyncDate()

    fun getCurrentStoreFront(country: String): String
}