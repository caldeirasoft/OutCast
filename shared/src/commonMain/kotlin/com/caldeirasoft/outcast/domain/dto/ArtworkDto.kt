package com.caldeirasoft.outcast.domain.dto

import com.caldeirasoft.outcast.domain.models.store.Artwork
import kotlinx.serialization.Serializable

@Serializable
class ArtworkDto (
    val url: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val textColor1: String? = null,
    val textColor2: String? = null,
    val textColor3: String? = null,
    val textColor4: String? = null,
    val bgColor: String? = null,
    val hasAlpha: Boolean? = null,
) {
    fun toArtwork() =
        Artwork(
            width = width,
            height = height,
            url = url,
            bgColor = bgColor,
            textColor1 = textColor2,
            textColor2 = textColor4
        )

    companion object {
        fun toArtwork(dto: ArtworkDto) =
            Artwork(
                width = dto.width,
                height = dto.height,
                url = dto.url,
                bgColor = dto.bgColor,
                textColor1 = dto.textColor1,
                textColor2 = dto.textColor4
            )
    }
}