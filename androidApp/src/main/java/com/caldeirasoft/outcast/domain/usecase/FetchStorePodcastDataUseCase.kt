package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchStorePodcastDataUseCase @Inject constructor(
    val storeRepository: StoreRepository,
) : NetworkUseCase<FetchStorePodcastDataUseCase.Params, StorePodcast>(
    fetchNetworkCall = { storeRepository.getPodcastDataAsync(
        it.url,
        it.storeFront
    )}
) {
    data class Params(
        val url: String,
        val storeFront: String)
}