package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class LoadStorePagingDataUseCase(
    val storeRepository: StoreRepository,
) {
    fun executeAsync(
        scope: CoroutineScope,
        storeData: StoreData,
        storeFront: String,
        newVersionAvailable: (() -> Unit)? = null,
        dataLoadedCallback: ((StorePage) -> Unit)? = null,
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
                        when {
                            storeData.genreId != null -> storeRepository.getGroupingDataAsync(
                                scope,
                                storeData.genreId,
                                storeFront,
                                newVersionAvailable)
                            storeData.url.isNotEmpty() -> storeRepository.getStoreDataAsync(
                                storeData.url,
                                storeFront)
                            else -> storeData.getPage()
                        }
                    },
                    dataLoadedCallback = dataLoadedCallback,
                    getStoreItems = storeRepository::getListStoreItemDataAsync
                )
            }
        ).flow
}