package com.caldeirasoft.outcast.data.api

import com.caldeirasoft.outcast.domain.dto.LockupResult
import com.caldeirasoft.outcast.domain.dto.ResultIdsResult
import com.caldeirasoft.outcast.domain.dto.StorePageDto
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
    suspend fun groupingData(@Header("X-Apple-Store-Front") storeFront: String, @Path("genre") genre: Int = 26): Response<com.caldeirasoft.outcast.domain.dto.StorePageDto>

    @GET()
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun storeData(@Header("X-Apple-Store-Front") storeFront: String, @Url url: String): Response<com.caldeirasoft.outcast.domain.dto.StorePageDto>

    @GET("/WebObjects/MZStoreServices.woa/ws/charts")
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun topCharts(
        @Header("X-Apple-Store-Front") storeFront: String,
        @Query("name") name: String,
        @Query("limit") limit: Int,
        @Query("g") genre: Int = 26,
    ): Response<com.caldeirasoft.outcast.domain.dto.ResultIdsResult>

    @GET("https://uclient-api.itunes.apple.com/WebObjects/MZStorePlatform.woa/wa/lookup?version=2&p=lockup&caller=DI6")
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json")
    suspend fun lookup(
        @Header("X-Apple-Store-Front") storeFront: String,
        @Query("id") ids: String,
    ): Response<com.caldeirasoft.outcast.domain.dto.LockupResult>

}