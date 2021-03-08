package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreTopCharts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class LoadStoreTopChartsPagingDataUseCase(
    private val storeRepository: StoreRepository
) {
    fun execute(
        scope: CoroutineScope,
        storeGenre: Int?,
        storeItemType: StoreItemType,
        storeFront: String,
        dataLoadedCallback: ((StoreTopCharts) -> Unit)?): Flow<PagingData<StoreItem>> =
        storeRepository.getTopChartPagingData(scope, storeGenre, storeItemType, storeFront, dataLoadedCallback)
}