package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.interfaces.StoreCollectionPodcastsEpisodes
import com.caldeirasoft.outcast.domain.models.StoreCollectionPodcasts
import com.caldeirasoft.outcast.domain.models.StoreGroupingData
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchStoreDirectoryUseCase @Inject constructor(
    val storeRepository: StoreRepository,
) : UseCase<FetchStoreDirectoryUseCase.Params, StoreGroupingData> {

    override fun invoke(params: Params): Flow<StoreGroupingData> = flow {
        when (val response = storeRepository.getDirectoryDataAsync(params.storeFront))
        {
            is NetworkResponse.Success -> {
                val storeDataGrouping = response.body
                emit(storeDataGrouping)

                storeDataGrouping.storeList
                    .filterIsInstance<StoreCollectionPodcastsEpisodes>()
                    .forEach { collection ->
                        val collResponse = storeRepository.getListStoreItemDataAsync(collection.itemsIds, params.storeFront, storeDataGrouping)
                        if (collResponse is NetworkResponse.Success) {
                            collection.items = collResponse.body
                            emit(storeDataGrouping)
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

    data class Params(val storeFront: String)
}