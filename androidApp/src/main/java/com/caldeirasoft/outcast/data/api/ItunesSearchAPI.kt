package com.caldeirasoft.outcast.data.api

import com.caldeirasoft.outcast.data.dto.ResultIdsResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

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
    ): Response<ResultIdsResult>

    @GET("/WebObjects/MZSearchHints.woa/wa/hints?clientApplication=Podcasts&f=json")
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun searchHints(
        @Header("X-Apple-Store-Front") storeFront: String,
        @Query("term") term: String,
    ): Response<ResultIdsResult>
}