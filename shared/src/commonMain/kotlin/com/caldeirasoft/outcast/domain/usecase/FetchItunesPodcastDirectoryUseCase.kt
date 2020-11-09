package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.repository.ItunesRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow

class FetchItunesPodcastDirectoryUseCase(
    val itunesRepository: ItunesRepository,
) : UseCase<FetchItunesPodcastDirectoryUseCase.Params, Resource<StoreDataGrouping>> {

    override fun invoke(params: Params): Flow<Resource<StoreDataGrouping>> =
        networkCall(
            fetchFromRemote = { itunesRepository.getPodcastDirectoryDataAsync(params.storeFront) },
        )

    data class Params(val storeFront: String)
}