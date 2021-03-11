package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class LoadStoreDirectoryPagingDataUseCase(
    val storeRepository: StoreRepository,
) {
    fun executeAsync(
        scope: CoroutineScope,
        storeFront: String,
        newVersionAvailable: () -> Unit,
        dataLoadedCallback: ((StorePage) -> Unit)?
    ): Flow<PagingData<StoreItem>> =
        Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
                prefetchDistance = 2
            ),
            pagingSourceFactory = {
                StoreDataPagingSource(
                    scope = scope,
                    loadDataFromNetwork = {
                        storeRepository.loadStoreDirectoryData(
                            scope,
                            storeFront,
                            newVersionAvailable)
                            .also {
                                dataLoadedCallback?.invoke(it)
                            }
                    },
                    dataLoadedCallback = null,
                    getStoreItems = storeRepository::getListStoreItemDataAsync
                )
            }
        ).flow

}