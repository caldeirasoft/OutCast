package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.models.Artwork
import kotlin.Long
import kotlin.String

data class EpisodesCountByPodcast(
  val podcastId: Long,
  val podcastName: String,
  val artwork: Artwork?,
  val episodeCount: Long
)