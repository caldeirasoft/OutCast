@file:UseSerializers(InstantSerializer::class)

package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import android.os.Parcelable
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.UseSerializers


@Parcelize
data class StorePodcastArg(
    val id: Long,
    val name: String,
    val url: String,
    val artistName: String,
    val artistId: Long? = null,
    val artistUrl: String? = null,
    val artwork: ArtworkArg?,
    val userRating: Float,
    val storeFront: String,
) : Parcelable {
    fun toStorePodcast() =
        StorePodcast(
            id = id,
            name = name,
            url = url,
            artistName = artistName,
            artistId = artistId,
            artistUrl = artistUrl,
            description = null,
            feedUrl = "",
            releaseDate = Clock.System.now(),
            releaseDateTime = Clock.System.now(),
            artwork = artwork?.run {
                Artwork(url, width, height, textColor1, textColor2, bgColor)
            },
            trackCount = 0,
            podcastWebsiteUrl = "",
            copyright = "",
            contentAdvisoryRating = "",
            userRating = userRating,
            genre = null,
            storeFront = storeFront
        )

    companion object {
        fun StorePodcast.toStorePodcastArg() = StorePodcastArg(
            id = id,
            name = name,
            url = url,
            artistId = artistId,
            artistName = artistName,
            artistUrl = artistUrl,
            artwork = artwork?.run {
                ArtworkArg(url, width, height, textColor1, textColor2, bgColor)
            },
            userRating = userRating,
            storeFront = storeFront,
        )
    }

    @Parcelize
    class ArtworkArg(
        val url: String,
        val width: Int,
        val height: Int,
        val textColor1: String? = null,
        val textColor2: String? = null,
        val bgColor: String? = null,
    ) : Parcelable
}