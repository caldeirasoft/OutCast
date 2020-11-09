package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.models.StoreDataRoom
import com.caldeirasoft.outcast.domain.repository.ItunesRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow

class FetchItunesRoomDataUseCase(
    val itunesRepository: ItunesRepository,
) : UseCase<FetchItunesRoomDataUseCase.Params, Resource<StoreDataRoom>> {

    override fun invoke(params: Params): Flow<Resource<StoreDataRoom>> =
        networkCall(
            fetchFromRemote = { itunesRepository.getRoomDataAsync(params.url, params.storeFront) },
        )

    data class Params(
        val url: String,
        val storeFront: String)
}