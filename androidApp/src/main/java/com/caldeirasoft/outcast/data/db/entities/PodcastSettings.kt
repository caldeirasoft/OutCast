@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.data.db.entities

import androidx.room.*
import com.caldeirasoft.outcast.domain.enums.*
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.format.DateTimeFormatter

@Serializable
@Entity(
  tableName = PodcastSettings.TABLE_NAME,
)
data class PodcastSettings(
  @PrimaryKey @ColumnInfo(name = "feedUrl") val feedUrl: String,
  @ColumnInfo(name = "isFollowed") val isFollowed: Boolean = false,
  @ColumnInfo(name = "followed_at") val followedAt: Instant? = null,
  @ColumnInfo(name = "notifications") val notifications: Boolean = true,
  @ColumnInfo(name = "new_episodes") val newEpisodesOptions: Int = NewEpisodesOptions.ADD_TO_INBOX.ordinal,
  @ColumnInfo(name = "episode_limit") val episodeLimit: Int = PodcastEpisodeLimitOptions.DEFAULT_SETTING.ordinal,
  @ColumnInfo(name = "skip_intro") val skipIntro: Int = 0,
  @ColumnInfo(name = "skip_outro") val skipOutro: Int = 0,
  @ColumnInfo(name = "podcast_filter") val podcastFilter: Int = PodcastFilter.ALL.ordinal,
  @ColumnInfo(name = "podcast_sort") val podcastSortOrder: Int = SortOrder.DESC.ordinal,
) {
  companion object {
    const val TABLE_NAME: String = "podcast_settings"

    // general
    // notifications
    // downloads
      // delete episode : after listening/1 day after listening/7 days after listening
    // playback
  }
}
