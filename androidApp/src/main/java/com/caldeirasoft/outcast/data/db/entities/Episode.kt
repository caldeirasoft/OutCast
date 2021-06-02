@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.data.db.entities

import android.os.Parcelable
import androidx.room.*
import com.caldeirasoft.outcast.data.db.customparcelers.InstantParceler
import com.caldeirasoft.outcast.data.db.customparcelers.NullableInstantParceler
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers

@Serializable
@Parcelize
@TypeParceler<Instant, InstantParceler>
@TypeParceler<Instant?, NullableInstantParceler>
@Entity(
  tableName = "episode",
  primaryKeys = ["feedUrl", "guid"],
  foreignKeys = [
    ForeignKey(
      entity = Podcast::class,
      parentColumns = ["feedUrl"],
      childColumns = ["feedUrl"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [
    Index(value = ["podcastId"])
  ]
)
data class Episode(
  @ColumnInfo(name = "feedUrl") val feedUrl: String,
  @ColumnInfo(name = "guid") val guid: String,
  @ColumnInfo(name = "name") val name: String,
  @ColumnInfo(name = "url") val url: String = "",
  @ColumnInfo(name = "podcastName") val podcastName: String,
  @ColumnInfo(name = "artistName") val artistName: String,
  @ColumnInfo(name = "podcastId") val podcastId: Long? = null,
  @ColumnInfo(name = "artistId") val artistId: Long? = null,
  @ColumnInfo(name = "releaseDateTime") val releaseDateTime: Instant,
  @ColumnInfo(name = "description") val description: String?,
  @ColumnInfo(name = "artworkUrl") val artworkUrl: String,
  @ColumnInfo(name = "mediaUrl") val mediaUrl: String,
  @ColumnInfo(name = "mediaType") val mediaType: String,
  @ColumnInfo(name = "duration") val duration: Int,
  @ColumnInfo(name = "category") val category: Int? = null,
  @ColumnInfo(name = "podcastEpisodeSeason") val podcastEpisodeSeason: Int? = null,
  @ColumnInfo(name = "podcastEpisodeNumber") val podcastEpisodeNumber: Int? = null,
  @ColumnInfo(name = "podcastEpisodeWebsiteUrl") val podcastEpisodeWebsiteUrl: String? = null,
  @ColumnInfo(name = "podcastEpisodeType") val podcastEpisodeType: String? = null,
  @ColumnInfo(name = "isExplicit") val isExplicit: Boolean = false,
  @ColumnInfo(name = "updatedAt") val updatedAt: String,

  // save
  @ColumnInfo(name = "isSaved") val isSaved: Boolean = false,
  @ColumnInfo(name = "saved_at") val savedAt: String? = null,

  // playback
  @ColumnInfo(name = "playback_state") val playbackState: Int = 0,
  @ColumnInfo(name = "playback_position") val playbackPosition: Int? = null,
  @ColumnInfo(name = "playback_played_at") val playedAt: String? = null,
) : Parcelable {

  @Transient
  val isFinished: Boolean
    get() = playbackPosition?.let { it >= duration } ?: false

  val hasBeenStarted: Boolean
    get() = playbackPosition?.let { it >= 0 } ?: false

  val playedAtInstant: Instant?
    get() = playedAt?.let { Instant.parse(it) }

}

