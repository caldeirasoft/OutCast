package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.StoreData
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow

class FetchStoreDataUseCase(
    val storeRepository: StoreRepository,
) : UseCase<FetchStoreDataUseCase.Params, Resource<StoreData>> {

    override fun invoke(params: Params): Flow<Resource<StoreData>> =
        networkCall(
            fetchFromRemote = { storeRepository.getStoreDataAsync(params.url, params.storeFront) },
        )

    data class Params(
        val url: String,
        val storeFront: String)
}