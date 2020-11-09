package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.ItunesRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkBoundResource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow

class FetchItunesListStoreItemsUseCase(
    val itunesRepository: ItunesRepository,
) : UseCase<FetchItunesListStoreItemsUseCase.Params, Resource<List<StoreItem>>> {

    override fun invoke(params: Params): Flow<Resource<List<StoreItem>>> =
        networkCall(
            fetchFromRemote = { itunesRepository.getListStoreItemDataAsync(params.lookupIds, params.storeFront, params.storeDataWithLookup) },
        )

    data class Params(
        val lookupIds: List<Long>,
        val storeDataWithLookup: StoreDataWithLookup,
        val storeFront: String)
}