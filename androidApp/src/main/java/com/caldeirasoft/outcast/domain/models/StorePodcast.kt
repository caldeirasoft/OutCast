@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.enums.NewEpisodesAction
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers

@Serializable
val StorePodcast.podcast: Podcast
    get() = Podcast(
            podcastId = id,
            name = name,
            artistName = artistName,
            url = url,
            category = category,
            artworkUrl = getArtworkUrl(),
            artworkDominantColor = artwork?.bgColor,
            artistId = artistId,
            artistUrl = artistUrl,
            copyright = copyright,
            description = description,
            feedUrl = feedUrl,
            podcastWebsiteURL = podcastWebsiteUrl,
            releaseDateTime = releaseDateTime,
            trackCount = trackCount.toLong(),
            updatedAt = releaseDateTime,
            userRating = userRating.toDouble(),
            isExplicit = isExplicit,
            newFeedUrl = null,
            isComplete = false,
            isSubscribed = false,
            newEpisodeAction = NewEpisodesAction.CLEAR,
        )