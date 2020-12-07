package com.caldeirasoft.outcast.domain.repository

import com.caldeirasoft.outcast.domain.models.StoreDirectory
import com.caldeirasoft.outcast.domain.models.StoreGroupingData
import com.caldeirasoft.outcast.domain.models.StoreTopCharts
import kotlinx.coroutines.flow.Flow

interface LocalCacheRepository {
    val directory : Flow<StoreDirectory?>

    suspend fun saveDirectory(storeData: StoreDirectory)
}