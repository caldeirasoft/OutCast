package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.LocalCacheRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import com.caldeirasoft.outcast.domain.util.stopwatch
import kotlinx.coroutines.flow.Flow

class FetchStoreDirectoryUseCase(
    val storeRepository: StoreRepository,
    val localCacheRepository: LocalCacheRepository
) {
    fun executeAsync(storeFront: String): Flow<Resource> =
        networkCall {
            val groupingData = stopwatch("FetchStoreGroupingUseCase - fetchGroupingData") {
                storeRepository.getGroupingDataAsync(null, storeFront)
            }.also { groupingData ->
                groupingData.genres?.let { genres ->
                    groupingData.storeList.add(2, genres)
                }
            }
            groupingData
        }

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