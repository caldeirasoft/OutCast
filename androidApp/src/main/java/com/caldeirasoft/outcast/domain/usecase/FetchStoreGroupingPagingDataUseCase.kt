package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.LocalCacheRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import kotlinx.coroutines.flow.Flow

class FetchStoreGroupingPagingDataUseCase(
    val storeRepository: StoreRepository,
    val localCacheRepository: LocalCacheRepository
) {
    fun executeAsync(
        genre: Int?, storeFront: String, dataLoadedCallback: ((StorePage) -> Unit)?
    ): Flow<PagingData<StoreItem>> =
        storeRepository.getGroupingPagingData(genre, storeFront, dataLoadedCallback)
}