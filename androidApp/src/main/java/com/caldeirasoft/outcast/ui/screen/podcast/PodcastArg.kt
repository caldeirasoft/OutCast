@file:UseSerializers(InstantSerializer::class)

package com.caldeirasoft.outcast.ui.screen.podcast

import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers


@Serializable
data class PodcastArg(
    val id: Long,
    val name: String,
    val url: String,
    val artistName: String,
    val artistId: Long? = null,
    val artistUrl: String? = null,
    val description: String? = null,
    @Serializable(with = InstantSerializer::class)
    val releaseDateTime: Instant,
    val artwork: Artwork?,
    val trackCount: Int,
    val genre: Genre?,
) {
}