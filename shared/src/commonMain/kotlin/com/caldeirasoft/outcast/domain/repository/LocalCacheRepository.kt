package com.caldeirasoft.outcast.domain.repository

import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import kotlinx.coroutines.flow.Flow

interface LocalCacheRepository {
    val storeDirectory : Flow<StoreGroupingPage?>

    suspend fun saveDirectory(storeData: StoreGroupingPage)
}