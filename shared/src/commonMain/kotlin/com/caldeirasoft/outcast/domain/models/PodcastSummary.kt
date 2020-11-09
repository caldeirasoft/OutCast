package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.domain.models.Artwork
import kotlin.Long
import kotlin.String

data class PodcastSummary(
  val podcastId: Long,
  val name: String,
  val artwork: Artwork?
)