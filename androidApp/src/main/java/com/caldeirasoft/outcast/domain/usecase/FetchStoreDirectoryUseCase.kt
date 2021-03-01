package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LocalCacheRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import kotlinx.coroutines.CoroutineScope

class FetchStoreDirectoryUseCase(
    val storeRepository: StoreRepository,
    val localCacheRepository: LocalCacheRepository
) {
    suspend fun executeAsync(
        scope: CoroutineScope,
        storeFront: String
    ): StoreGroupingPage =
        storeRepository.getGroupingDataFromNetworkOrLocalStorage(scope, storeFront, null)

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