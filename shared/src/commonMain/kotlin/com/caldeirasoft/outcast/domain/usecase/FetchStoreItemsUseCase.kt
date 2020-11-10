package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow

class FetchStoreItemsUseCase(
    val storeRepository: StoreRepository,
) : UseCase<FetchStoreItemsUseCase.Params, Resource<List<StoreItem>>> {

    override fun invoke(params: Params): Flow<Resource<List<StoreItem>>> =
        networkCall(
            fetchFromRemote = { storeRepository.getListStoreItemDataAsync(params.lookupIds, params.storeFront, params.storeDataWithLookup) },
        )

    data class Params(
        val lookupIds: List<Long>,
        val storeDataWithLookup: StoreDataWithLookup,
        val storeFront: String)
}