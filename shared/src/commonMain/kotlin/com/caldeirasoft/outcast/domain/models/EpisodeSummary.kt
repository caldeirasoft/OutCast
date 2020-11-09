package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.models.Artwork
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlinx.datetime.Instant

data class EpisodeSummary(
  val episodeId: Long,
  val name: String,
  val podcastId: Long,
  val releaseDateTime: String,
  val description: String?,
  val contentAdvisoryRating: String?,
  val artwork: Artwork?,
  val duration: Long,
  val podcastEpisodeSeason: Long?,
  val podcastEpisodeNumber: Long?,
  val isFavorite: Boolean,
  val isPlayed: Boolean,
  val playbackPosition: Long?,
  val isInQueue: Long,
  val isInInbox: Long,
  val isInHistory: Long,
  val updatedAt: Instant
)