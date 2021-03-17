package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.EpisodeSummary
import com.caldeirasoft.outcast.db.EpisodeWithInfos
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork

fun Episode.getArtworkUrl(): String =
    StoreItemWithArtwork.artworkUrl(artwork, 200, 200)

fun EpisodeSummary.getArtworkUrl(): String =
    StoreItemWithArtwork.artworkUrl(artwork, 200, 200)

val EpisodeWithInfos.episode: Episode
    get() = Episode(
        episodeId = this.episodeId,
        name = this.name,
        url = this.url,
        podcastId = this.podcastId,
        podcastName = this.podcastName,
        artistName = this.artistName,
        artistId = this.artistId,
        description = this.description,
        genre = this.genre,//episodeEntry.genres.map { it.toGenre() }.first(),
        feedUrl = this.feedUrl,
        releaseDateTime = this.releaseDateTime,
        artwork = this.artwork,
        contentAdvisoryRating = this.contentAdvisoryRating,
        mediaUrl = this.mediaUrl,
        mediaType = this.mediaType,
        duration = this.duration,
        podcastEpisodeNumber = this.podcastEpisodeNumber,
        podcastEpisodeSeason = this.podcastEpisodeSeason,
        podcastEpisodeType = this.podcastEpisodeType,
        podcastEpisodeWebsiteUrl = this.podcastEpisodeWebsiteUrl,
        updatedAt = this.updatedAt
    )

val EpisodeSummary.episode: Episode
    get() = Episode(
        episodeId = this.episodeId,
        name = this.name,
        url = this.url,
        podcastId = this.podcastId,
        podcastName = this.podcastName,
        artistName = "",
        artistId = 0L,
        description = this.description,
        genre = emptyList(),//episodeEntry.genres.map { it.toGenre() }.first(),
        feedUrl = "",
        releaseDateTime = this.releaseDateTime,
        artwork = this.artwork,
        contentAdvisoryRating = this.contentAdvisoryRating,
        mediaUrl = "",
        mediaType = "",
        duration = this.duration,
        podcastEpisodeNumber = this.podcastEpisodeNumber,
        podcastEpisodeSeason = this.podcastEpisodeSeason,
        podcastEpisodeType = this.podcastEpisodeType,
        podcastEpisodeWebsiteUrl = "",
        updatedAt = this.updatedAt
    )