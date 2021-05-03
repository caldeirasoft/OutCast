package com.caldeirasoft.outcast.data.db.entities

import android.os.Parcelable
import androidx.room.*
import com.caldeirasoft.outcast.data.db.entities.Podcast.Companion.toPodcast
import com.caldeirasoft.outcast.domain.models.episode
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class EpisodeWithPodcast(
  @Embedded val episode: Episode,
  @Relation(
    entity = Podcast::class,
    parentColumn = "feedUrl",
    entityColumn = "feedUrl"
  )
  val podcast: Podcast,
) {
  companion object {
    fun StoreEpisode.toEpisodeWithPodcast() =
      EpisodeWithPodcast(
        episode = this.episode,
        podcast = this.storePodcast.toPodcast()
      )
  }
}