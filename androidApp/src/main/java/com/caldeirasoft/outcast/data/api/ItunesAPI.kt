package com.caldeirasoft.outcast.data.api

import com.caldeirasoft.outcast.domain.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ItunesAPI {
    companion object {
        internal const val baseUrl = "https://itunes.apple.com"
    }

    @GET("/genre/id{genre}")
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun groupingData(@Header("X-Apple-Store-Front") storeFront: String, @Path("genre") genre: Int = 26): Response<StorePageDto>

    @GET()
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun storeData(@Header("X-Apple-Store-Front") storeFront: String, @Url url: String): Response<StorePageDto>

    @GET("https://search.itunes.apple.com/WebObjects/MZStore.woa/wa/search")
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun searchData(
        @Header("X-Apple-Store-Front") storeFront: String,
        @Query("term") term: String,
        @Query("clientApplication") clientApplication: String = "Podcasts",
    ): Response<StorePageDto>

    @GET("https://search.itunes.apple.com/WebObjects/MZSearchHints.woa/wa/hints?f=json")
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun searchHints(
        @Header("X-Apple-Store-Front") storeFront: String,
        @Query("term") term: String,
        @Query("clientApplication") clientApplication: String = "Podcasts",
    ): Response<List<SearchHintResult>>

    @GET("/WebObjects/MZStoreServices.woa/ws/genres")
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun genres(
        @Header("X-Apple-Store-Front") storeFront: String,
        @Query("id") genre: Int = 26,
    ): Response<Map<Int, GenreResult>>

    @GET("/WebObjects/MZStoreServices.woa/ws/charts")
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun topChartsIds(
        @Header("X-Apple-Store-Front") storeFront: String,
        @Query("name") name: String,
        @Query("limit") limit: Int,
        @Query("g") genre: Int = 26,
    ): Response<ResultIdsResult>

    @GET("/WebObjects/MZStore.woa/wa/viewTop")
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun topCharts(
        @Header("X-Apple-Store-Front") storeFront: String,
        @Query("genreId") genre: Int = 26,
    ): Response<StorePageDto>

    @GET("https://uclient-api.itunes.apple.com/WebObjects/MZStorePlatform.woa/wa/lookup?version=2&p=lockup&caller=DI6")
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun lookup(
        @Header("X-Apple-Store-Front") storeFront: String,
        @Query("id") ids: String,
    ): Response<LockupResult>

}