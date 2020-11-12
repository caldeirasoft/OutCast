package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.StoreCollectionPodcasts
import com.caldeirasoft.outcast.domain.models.StoreData
import com.caldeirasoft.outcast.domain.models.StoreDataGrouping
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchStoreDirectoryUseCase(
    val storeRepository: StoreRepository,
) : UseCase<FetchStoreDirectoryUseCase.Params, Resource<StoreDataGrouping>> {

    override fun invoke(params: Params): Flow<Resource<StoreDataGrouping>> = flow<Resource<StoreDataGrouping>> {
        emit(Resource.Loading(null))

        when (val response = storeRepository.getDirectoryDataAsync(params.storeFront))
        {
            is NetworkResponse.Success -> {
                val storeDataGrouping = response.body
                emit(Resource.Success(storeDataGrouping))

                storeDataGrouping.storeList
                    .filterIsInstance<StoreCollectionPodcasts>()
                    .forEach { collection ->
                        val collResponse = storeRepository.getListStoreItemDataAsync(collection.itemsIds, params.storeFront, storeDataGrouping)
                        if (collResponse is NetworkResponse.Success) {
                            val newStoreDataGrouping = StoreDataGrouping(
                                id = storeDataGrouping.id,
                                label = storeDataGrouping.label,
                                storeList = storeDataGrouping.storeList,
                                lookup = storeDataGrouping.lookup
                            )
                            newStoreDataGrouping.storeList
                                .filterIsInstance<StoreCollectionPodcasts>()
                                .find { it.itemsIds == collection.itemsIds }
                                ?.items = collResponse.body
                            emit(Resource.Success(newStoreDataGrouping))
                        }
                    }
            }
            is NetworkResponse.NetworkError -> emit(Resource.Error(response.error.message ?: "Network error"))
            is NetworkResponse.ServerError -> emit(Resource.Error(response.code.toString()))
            is NetworkResponse.UnknownError -> emit(Resource.ErrorThrowable(response.error))
            else -> println("unknown")
        }
    }

    data class Params(val storeFront: String)
}