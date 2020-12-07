package com.caldeirasoft.outcast.domain.repository

import com.caldeirasoft.outcast.domain.models.store.StoreDirectory
import kotlinx.coroutines.flow.Flow

interface LocalCacheRepository {
    val directory : Flow<StoreDirectory?>

    suspend fun saveDirectory(storeData: StoreDirectory)
}