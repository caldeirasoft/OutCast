package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.interfaces.StoreCollectionPodcastsEpisodes
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.models.StoreMultiRoom
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchStoreDataUseCase @Inject constructor(
    val storeRepository: StoreRepository,
) : NetworkUseCase<FetchStoreDataUseCase.Params, StoreData>(
    fetchNetworkCall = { storeRepository.getStoreDataAsync(it.url, it.storeFront) }
) {
    data class Params(
        val url: String,
        val storeFront: String)
}