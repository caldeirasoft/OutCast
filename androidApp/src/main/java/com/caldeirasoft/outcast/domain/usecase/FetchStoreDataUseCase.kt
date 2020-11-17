package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.interfaces.StoreCollectionPodcastsEpisodes
import com.caldeirasoft.outcast.domain.models.StoreCollectionPodcasts
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.models.StoreMultiRoom
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchStoreDataUseCase @Inject constructor(
    val storeRepository: StoreRepository,
) : UseCase<FetchStoreDataUseCase.Params, StoreData> {

    override fun invoke(params: Params): Flow<StoreData> = flow {
        when (val response = storeRepository.getStoreDataAsync(params.url, params.storeFront))
        {
            is NetworkResponse.Success -> {
                val storeData = response.body
                emit(storeData)

                when (storeData) {
                    is StoreMultiRoom -> {
                        storeData.storeList
                            .filterIsInstance<StoreCollectionPodcastsEpisodes>()
                            .forEach { collection ->
                                val collResponse = storeRepository.getListStoreItemDataAsync(collection.itemsIds, params.storeFront, storeData)
                                if (collResponse is NetworkResponse.Success) {
                                    collection.items = collResponse.body
                                    emit(storeData)
                                }
                            }

                    }
                }
            }
            is NetworkResponse.NetworkError ->
                throw Exception(response.error.message)
            is NetworkResponse.ServerError ->
                throw Exception(response.code.toString())
            is NetworkResponse.UnknownError ->
                throw Exception(response.error)
            else -> println("unknown")
        }
    }

    data class Params(
        val url: String,
        val storeFront: String)
}