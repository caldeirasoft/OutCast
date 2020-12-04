package com.caldeirasoft.outcast.domain.repository

import com.caldeirasoft.outcast.domain.models.StoreGroupingData
import com.caldeirasoft.outcast.domain.models.StoreTopCharts
import kotlinx.coroutines.flow.Flow

interface LocalCacheRepository {
    val directory : Flow<StoreGroupingData>

    val topCharts : Flow<StoreTopCharts>

    suspend fun saveDirectory(storeData: StoreGroupingData)

    suspend fun saveTopCharts(storeData: StoreTopCharts)
}