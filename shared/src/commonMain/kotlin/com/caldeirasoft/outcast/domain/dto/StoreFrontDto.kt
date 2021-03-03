package com.caldeirasoft.outcast.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class StoreFrontDto(
    @SerialName("storefronts") val storeFronts: List<StoreFrontResult> = arrayListOf(),
    val languages: List<LanguageResult> = arrayListOf()
)

@Serializable
class StoreFrontResult(
    val type: String = "",
    @SerialName("country-code") val countryCode: String = "",
    @SerialName("display-name") val displayName: String = "",
    val id: Int = 0,
    val url: String = "",
    val languages: List<String> = arrayListOf()
)

@Serializable
class LanguageResult (
    val type: String = "",
    val name: String = "",
    val id: String = "",
)
