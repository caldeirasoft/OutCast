@file:UseSerializers(InstantStringSerializer::class, LocalDateStringSerializer::class)

package com.caldeirasoft.outcast.domain.dto

import com.caldeirasoft.outcast.domain.serializers.InstantStringSerializer
import com.caldeirasoft.outcast.domain.serializers.LocalDateStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

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
