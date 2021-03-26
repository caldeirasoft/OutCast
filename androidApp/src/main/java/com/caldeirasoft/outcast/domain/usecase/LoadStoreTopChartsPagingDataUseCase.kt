package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.data.util.StoreChartsPagingSource
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class LoadStoreTopChartsPagingDataUseCase(
    private val storeRepository: StoreRepository
) {
    fun execute(
        scope: CoroutineScope,
        genreId: Int?,
        storeItemType: StoreItemType,
        storeFront: String,
    ): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 200,
                prefetchDistance = 5
            )
        ) {
            StoreChartsPagingSource(
                scope = scope,
                storeFront = storeFront,
                loadDataFromNetwork = {
                    storeRepository.getTopChartsIdsAsync(genreId,
                        storeFront,
                        storeItemType,
                        200)
                },
                getStoreItems = storeRepository::getListStoreItemDataAsync,
            )
        }.flow
}