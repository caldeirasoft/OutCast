package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GetStoreItemsUseCase(
    val storeRepository: StoreRepository
) {
    suspend fun execute(lookupIds: List<Long>, storeFront: String, storePage: StorePage?): List<StoreItem> =
        storeRepository.getListStoreItemDataAsync(
            lookupIds,
            storeFront,
            storePage
        )
}