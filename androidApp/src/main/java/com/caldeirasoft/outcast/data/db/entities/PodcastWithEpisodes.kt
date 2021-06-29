package com.caldeirasoft.outcast.data.db.entities

import androidx.room.*

data class PodcastWithEpisodes(
  @Embedded val podcast: Podcast,
  @Relation(
    parentColumn = "feedUrl",
    entityColumn = "feedUrl"
  )
  val episodes: List<Episode>
)
