package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class FetchStoreDirectoryPagingDataUseCase(
    val storeRepository: StoreRepository,
) {
    fun executeAsync(
        scope: CoroutineScope,
        storeFront: String, newVersionAvailable: () -> Unit
    ): Flow<PagingData<StoreItem>> =
        storeRepository.getDirectoryPagingData(scope, storeFront, newVersionAvailable)

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