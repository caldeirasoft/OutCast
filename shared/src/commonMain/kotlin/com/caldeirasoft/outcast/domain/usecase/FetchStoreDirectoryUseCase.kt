package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.StoreDataGrouping
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow

class FetchStoreDirectoryUseCase(
    val storeRepository: StoreRepository,
) : UseCase<FetchStoreDirectoryUseCase.Params, Resource<StoreDataGrouping>> {

    override fun invoke(params: Params): Flow<Resource<StoreDataGrouping>> =
        networkCall(
            fetchFromRemote = { storeRepository.getDirectoryDataAsync(params.storeFront) },
        )

    data class Params(val storeFront: String)
}