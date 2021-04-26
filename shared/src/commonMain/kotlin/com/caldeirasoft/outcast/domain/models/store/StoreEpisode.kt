@file:UseSerializers(InstantSerializer::class, DurationSerializer::class)

package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.serializers.DurationSerializer
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers

@Serializable
class StoreEpisode(
    override val id: Long,
    val feedUrl: String,
    val guid: String,
    val name: String,
    val url: String,
    val podcastId: Long,
    val podcastName: String,
    val artistName: String,
    val artistId: Long? = null,
    val releaseDateTime: Instant,
    val description: String? = null,
    val mediaUrl: String,
    val mediaType: String,
    val duration: Int,
    val playbackPosition: Int? = null,
    val podcastEpisodeSeason: Int? = null,
    val podcastEpisodeNumber: Int? = null,
    val podcastEpisodeWebsiteUrl: String? = null,
    val podcastEpisodeType: String = "",
    override val storeFront: String = "",
    override val artwork: Artwork? = null,
    val isExplicit: Boolean = false,
    val isComplete: Boolean = false,
    val storePodcast: StorePodcast,
) : StoreItemArtwork {
    override var featuredArtwork: Artwork? = null
    override var editorialArtwork: Artwork? = null

    override fun getArtworkUrl(): String =
        StoreItemArtwork.artworkUrl(artwork, 200, 200)
}