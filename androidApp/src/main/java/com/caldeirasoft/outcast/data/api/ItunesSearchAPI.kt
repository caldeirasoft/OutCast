package com.caldeirasoft.outcast.data.api

import com.caldeirasoft.outcast.domain.dto.ResultIdsResult
import com.caldeirasoft.outcast.domain.dto.StorePageDto
import retrofit2.Response
import retrofit2.http.*

interface ItunesSearchAPI {
    companion object {
        internal const val baseUrl = "https://search.itunes.apple.com"
    }

    @GET("/WebObjects/MZStore.woa/wa/search?clientApplication=Podcasts")
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun search(
        @Header("X-Apple-Store-Front") storeFront: String,
        @Query("term") term: String,
    ): Response<com.caldeirasoft.outcast.domain.dto.ResultIdsResult>

    @GET("/WebObjects/MZSearchHints.woa/wa/hints?clientApplication=Podcasts&f=json")
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun searchHints(
        @Header("X-Apple-Store-Front") storeFront: String,
        @Query("term") term: String,
    ): Response<com.caldeirasoft.outcast.domain.dto.ResultIdsResult>
}