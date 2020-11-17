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
) : UseCase<FetchStoreItemsUseCase.Params, NetworkResponse<List<StoreItem>>> {

    override fun invoke(params: Params): Flow<NetworkResponse<List<StoreItem>>> = flow {
        emit(storeRepository.getListStoreItemDataAsync(params.lookupIds, params.storeFront, params.storeDataWithLookup))
    }

    data class Params(
        val lookupIds: List<Long>,
        val storeDataWithLookup: StoreDataWithLookup,
        val storeFront: String)
}