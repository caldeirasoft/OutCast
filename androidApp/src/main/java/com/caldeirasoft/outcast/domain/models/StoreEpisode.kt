@file:UseSerializers(InstantSerializer::class, DurationSerializer::class)

package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.serializers.DurationSerializer
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.UseSerializers
import java.time.format.DateTimeFormatter

val StoreEpisode.episode: Episode
        get() = Episode(
            feedUrl = this.feedUrl,
            guid = this.guid,
            name = this.name,
            url = this.url,
            podcastId = this.podcastId,
            podcastName = this.podcastName,
            artistName = this.artistName,
            artistId = this.artistId,
            description = this.description,
            releaseDateTime = this.releaseDateTime,
            artworkUrl = getArtworkUrl(),
            mediaUrl = this.mediaUrl,
            mediaType = this.mediaType,
            duration = this.duration,
            podcastEpisodeNumber = this.podcastEpisodeNumber,
            podcastEpisodeSeason = this.podcastEpisodeSeason,
            podcastEpisodeType = this.podcastEpisodeType,
            podcastEpisodeWebsiteUrl = this.podcastEpisodeWebsiteUrl,
            isExplicit = false,
            updatedAt = Clock.System.now().toString(),
            playbackPosition = null,
            isSaved = false,
            playedAt = null,
        )