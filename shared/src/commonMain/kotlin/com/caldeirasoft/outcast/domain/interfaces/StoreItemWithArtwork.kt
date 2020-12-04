package com.caldeirasoft.outcast.domain.interfaces

import com.caldeirasoft.outcast.domain.models.Artwork

interface StoreItemWithArtwork : StoreItem {
    val artwork: Artwork?

    fun getArtworkUrl(): String;

    fun artworkUrl(width:Int, height: Int, crop:String = "bb", format:String = "jpg"): String {
        return artwork?.url?.replace("{w}", width.toString())
            ?.replace("{h}", height.toString())
            ?.replace("{c}", crop)
            ?.replace("{f}", format)
            .orEmpty()
    }
}