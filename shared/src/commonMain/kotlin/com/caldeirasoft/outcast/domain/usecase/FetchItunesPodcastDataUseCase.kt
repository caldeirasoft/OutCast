package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.ItunesRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkBoundResource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow

class FetchItunesPodcastDataUseCase(
    val itunesRepository: ItunesRepository,
) : UseCase<FetchItunesPodcastDataUseCase.Params, Resource<StoreDataPodcast>> {

    override fun invoke(params: Params): Flow<Resource<StoreDataPodcast>> =
        networkCall(
            fetchFromRemote = { itunesRepository.getPodcastDataAsync(params.url, params.storeFront) },
        )

    data class Params(
        val url: String,
        val storeFront: String)
}