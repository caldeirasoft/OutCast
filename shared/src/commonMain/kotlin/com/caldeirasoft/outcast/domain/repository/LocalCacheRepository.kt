package com.caldeirasoft.outcast.domain.repository

import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.StoreDirectory
import kotlinx.coroutines.flow.Flow

interface LocalCacheRepository {
    val storeDirectory : Flow<StoreDirectory?>

    suspend fun saveDirectory(storeData: StoreDirectory)
}