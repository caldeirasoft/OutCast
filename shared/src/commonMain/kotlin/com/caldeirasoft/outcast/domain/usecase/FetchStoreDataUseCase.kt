package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.StoreCollectionPodcasts
import com.caldeirasoft.outcast.domain.models.StoreData
import com.caldeirasoft.outcast.domain.models.StoreDataMultiRoom
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchStoreDataUseCase(
    val storeRepository: StoreRepository,
) : UseCase<FetchStoreDataUseCase.Params, Resource<StoreData>> {

    override fun invoke(params: Params): Flow<Resource<StoreData>> = flow {
        emit(Resource.Loading(null))

        when (val response = storeRepository.getStoreDataAsync(params.url, params.storeFront))
        {
            is NetworkResponse.Success -> {
                val storeData = response.body
                emit(Resource.Success(storeData))

                when (storeData) {
                    is StoreDataMultiRoom -> {
                        storeData.storeList
                            .filterIsInstance<StoreCollectionPodcasts>()
                            .forEach { collection ->
                                val collResponse = storeRepository.getListStoreItemDataAsync(collection.itemsIds, params.storeFront, storeData)
                                if (collResponse is NetworkResponse.Success) {
                                    collection.items = collResponse.body
                                    emit(Resource.Success(storeData))
                                }
                            }

                    }
                }
            }
            is NetworkResponse.NetworkError -> emit(Resource.Error<StoreData>(response.error.message ?: "Network error"))
            is NetworkResponse.ServerError -> emit(Resource.Error<StoreData>(response.code.toString() ?: "Server error"))
            is NetworkResponse.UnknownError -> emit(Resource.ErrorThrowable<StoreData>(response.error))
        }
    }

    data class Params(
        val url: String,
        val storeFront: String)
}