package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.StoreDirectory
import com.caldeirasoft.outcast.domain.models.StoreGroupingData
import com.caldeirasoft.outcast.domain.repository.LocalCacheRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkBoundResource
import com.caldeirasoft.outcast.domain.util.stopwatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class FetchStoreDirectoryUseCase(
    val storeRepository: StoreRepository,
    val localCacheRepository: LocalCacheRepository
) {
    fun execute(storeFront: String): Flow<Resource<StoreDirectory>> =
        networkBoundResource(
            fetchFromLocal = { localCacheRepository.directory },
            fetchFromRemote = {
                stopwatch("FetchStoreDirectoryUseCase - getDirectoryDataAsync") {
                    storeRepository.getDirectoryDataAsync(storeFront)
                }
            },
            shouldFetchFromRemote = {
                true
                //it?.let { true } ?: false
            },
            saveRemoteData = {
                stopwatch("FetchStoreDirectoryUseCase - saveDirectory") {
                    localCacheRepository.saveDirectory(it)
                }
            }
        )
}