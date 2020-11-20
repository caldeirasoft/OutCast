package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.interfaces.StoreDataWithLookup
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchStoreItemsUseCase @Inject constructor(
    val storeRepository: StoreRepository,
) : NetworkUseCase<FetchStoreItemsUseCase.Params, List<StoreItem>>(
    fetchNetworkCall = { storeRepository.getListStoreItemDataAsync(
        it.lookupIds,
        it.storeFront,
        it.storeDataWithLookup)
    }
) {
    data class Params(
        val lookupIds: List<Long>,
        val storeDataWithLookup: StoreDataWithLookup,
        val storeFront: String)
}