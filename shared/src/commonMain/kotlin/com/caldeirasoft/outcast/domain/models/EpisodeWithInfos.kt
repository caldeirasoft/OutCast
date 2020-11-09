package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.models.Artwork
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.datetime.Instant

data class EpisodeWithInfos(
  val episodeId: Long,
  val name: String,
  val podcastId: Long,
  val podcastName: String,
  val artistName: String,
  val artistId: Long?,
  val releaseDateTime: Instant,
  val genre: List<Int>,
  val feedUrl: String,
  val description: String?,
  val contentAdvisoryRating: String?,
  val artwork: Artwork?,
  val mediaUrl: String,
  val mediaType: String,
  val duration: Long,
  val podcastEpisodeSeason: Long?,
  val podcastEpisodeNumber: Long?,
  val podcastEpisodeWebsiteUrl: String?,
  val isFavorite: Boolean,
  val isPlayed: Boolean,
  val playbackPosition: Long?,
  val isInQueue: Long,
  val isInInbox: Long,
  val isInHistory: Long
)