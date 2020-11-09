package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.repository.ItunesRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow

class FetchItunesArtistDataUseCase(
    val itunesRepository: ItunesRepository,
) : UseCase<FetchItunesArtistDataUseCase.Params, Resource<StoreDataArtist>> {

    override fun invoke(params: Params): Flow<Resource<StoreDataArtist>> =
        networkCall(
            fetchFromRemote = { itunesRepository.getArtistDataAsync(params.url, params.storeFront) },
        )

    data class Params(
        val url: String,
        val storeFront: String)
}