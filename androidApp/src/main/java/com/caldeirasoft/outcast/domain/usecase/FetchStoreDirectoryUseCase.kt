package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.LocalCacheRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import kotlinx.coroutines.flow.Flow

class FetchStoreDirectoryUseCase(
    val storeRepository: StoreRepository,
    val localCacheRepository: LocalCacheRepository
) {
    fun executeAsync(
        storeFront: String, dataLoadedCallback: ((StorePage) -> Unit)?
    ): Flow<PagingData<StoreItem>> =
        storeRepository.getDirectoryPagingData(storeFront, dataLoadedCallback)

    /*
        networkBoundResource(
            fetchFromLocal = { localCacheRepository.storeDirectory },
            fetchFromRemote = {
                stopwatch("FetchStoreDirectoryUseCase - getDirectoryDataAsync") {
                    storeRepository.getDirectoryDataAsync(storeFront)
                }
            },
            shouldFetchFromRemote = {
                true
                /*it?.let {
                    !(it.storeFront == storeFront &&
                            it.timestamp.toLocalDateTime(TimeZone.UTC).date >= Clock.System.todayAt(TimeZone.UTC))
                } ?: true*/
            },
            saveRemoteData = {
                stopwatch("FetchStoreDirectoryUseCase - saveDirectory") {
                    localCacheRepository.saveDirectory(it)
                }
            }
        )
     */
}