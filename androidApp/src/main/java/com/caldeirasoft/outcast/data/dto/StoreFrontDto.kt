package com.caldeirasoft.outcast.data.dto

import com.squareup.moshi.Json

class StoreFrontDto(
    @Json(name = "storefronts") val storeFronts: List<StoreFrontResult> = arrayListOf(),
    val languages: List<LanguageResult> = arrayListOf()
)

class StoreFrontResult(
    val type: String = "",
    @Json(name = "country-code") val countryCode: String = "",
    @Json(name = "display-name") val displayName: String = "",
    val id: Int = 0,
    val url: String = "",
    val languages: List<String> = arrayListOf()
)

class LanguageResult (
    val type: String = "",
    val name: String = "",
    val id: String = "",
)
