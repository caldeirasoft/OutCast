package com.caldeirasoft.outcast.domain.models

data class EpisodesCountByPodcast(
  val podcastId: Long,
  val podcastName: String,
  val artwork: Artwork?,
  val episodeCount: Long
)