package com.caldeirasoft.outcast.domain.models

import kotlinx.datetime.Instant

data class Podcast(
    val podcastId: Long,
    val name: String,
    val url: String,
    val artistName: String,
    val artistId: Long?,
    val artistUrl: String?,
    val description: String?,
    val feedUrl: String,
    val releaseDateTime: Instant,
    val artwork: Artwork?,
    val trackCount: Long,
    val podcastWebsiteURL: String?,
    val copyright: String?,
    val contentAdvisoryRating: String?,
    val userRating: Double?,
    val genreId: Int?,
    val isSubscribed: Boolean,
    val newEpisodeAction: NewEpisodesAction,
    val updatedAt: Instant
)