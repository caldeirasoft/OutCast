package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class FetchStoreRoomPagingDataUseCase(
    val storeRepository: StoreRepository
) {
    fun executeAsync(
        scope: CoroutineScope,
        storeRoom: StoreRoom,
        dataLoadedCallback: ((StorePage) -> Unit)?
    ): Flow<PagingData<StoreItem>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                StoreDataPagingSource(
                    scope = scope,
                    loadDataFromNetwork = {
                        if (storeRoom.url.isEmpty()) storeRoom.getPage()
                        else storeRepository.getStoreDataAsync(storeRoom.url, storeRoom.storeFront)
                    },
                    dataLoadedCallback = dataLoadedCallback,
                    getStoreItems = storeRepository::getListStoreItemDataAsync
                )
            }
        ).flow
}