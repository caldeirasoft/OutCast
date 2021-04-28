package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadStorePagingDataUseCase @Inject constructor(
    val storeRepository: StoreRepository,
    val dataStoreRepository: DataStoreRepository
) {
    fun executeAsync(
        url: String,
        storeData: StoreData?,
        storeFront: String,
        newVersionAvailable: (() -> Unit)? = null,
        dataLoadedCallback: ((StoreData) -> Unit)? = null,
    ): Flow<PagingData<StoreItem>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                StoreDataPagingSource(
                    loadDataFromNetwork = {
                        when {
                            storeData == null -> storeRepository.getGroupingDataAsync(
                                storeData?.genreId,
                                storeFront,
                                newVersionAvailable)
                            url.isNotEmpty() -> storeRepository.getStoreDataAsync(
                                url,
                                storeFront)
                            else -> storeData
                        }
                    },
                    dataLoadedCallback = dataLoadedCallback,
                    getStoreItems = storeRepository::getListStoreItemDataAsync
                )
            }
        ).flow
}