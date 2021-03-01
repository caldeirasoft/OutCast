package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.StoreRepository
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
        storeRepository.getStoreRoomPagingData(scope, storeRoom, dataLoadedCallback)
}