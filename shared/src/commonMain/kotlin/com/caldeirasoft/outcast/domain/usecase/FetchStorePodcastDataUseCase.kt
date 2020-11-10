package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow

class FetchStorePodcastDataUseCase(
    val storeRepository: StoreRepository,
) : UseCase<FetchStorePodcastDataUseCase.Params, Resource<StoreDataPodcast>> {

    override fun invoke(params: Params): Flow<Resource<StoreDataPodcast>> =
        networkCall(
            fetchFromRemote = { storeRepository.getPodcastDataAsync(params.url, params.storeFront) },
        )

    data class Params(
        val url: String,
        val storeFront: String)
}