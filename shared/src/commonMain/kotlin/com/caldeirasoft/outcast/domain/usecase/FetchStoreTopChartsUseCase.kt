package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.models.StoreGroupingData
import com.caldeirasoft.outcast.domain.models.StoreTopCharts
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.DataState
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.*

class FetchStoreTopChartsUseCase(
    val storeRepository: StoreRepository,
) {
    fun execute(storeFront: String): Flow<StoreTopCharts> = flow {
        emit(storeRepository.getTopChartsAsync(storeFront))
    }
}