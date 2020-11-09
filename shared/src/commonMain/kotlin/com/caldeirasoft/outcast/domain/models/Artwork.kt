package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Artwork(val url: String,
                   val width: Int,
                   val height: Int,
                   val textColor1: String? = null,
                   val textColor2: String? = null,
                   val bgColor: String? = null) {
    fun getArtwork(desiredWidth: Int): String {
        return url.replace("{w}", desiredWidth.toString())
            .replace("{h}", (height * desiredWidth * 1f / width).toInt().toString())
            .replace("{c}", "fa") // fa or bb
            .replace("{f}", "jpg")
    }

    fun getArtwork(desiredWidth: Int, desiredHeight: Int, crop: String): String {
        return url.replace("{w}", desiredWidth.toString())
            .replace("{h}", desiredHeight.toString())
            .replace("{c}", crop) // fa or bb
            .replace("{f}", "jpg")
    }
}