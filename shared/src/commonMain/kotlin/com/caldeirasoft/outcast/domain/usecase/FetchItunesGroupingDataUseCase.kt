package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.repository.ItunesRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow

class FetchItunesGroupingDataUseCase(
    val itunesRepository: ItunesRepository,
) : UseCase<FetchItunesGroupingDataUseCase.Params, Resource<StoreDataGrouping>> {

    override fun invoke(params: Params): Flow<Resource<StoreDataGrouping>> =
        networkCall(
            fetchFromRemote = { itunesRepository.getGroupingDataAsync(params.url, params.storeFront) },
        )

    data class Params(
        val url: String,
        val storeFront: String)
}