package com.caldeirasoft.outcast.data.db.entities

import androidx.room.*
import com.caldeirasoft.outcast.domain.models.Category
import kotlinx.datetime.Instant

data class PodcastWithEpisodes(
  @Embedded val podcast: Podcast,
  @Relation(
    parentColumn = "feedUrl",
    entityColumn = "feedUrl"
  )
  val episodes: List<Episode>
)
