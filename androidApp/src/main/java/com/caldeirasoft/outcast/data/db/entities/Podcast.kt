@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.data.db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.caldeirasoft.outcast.data.db.customparcelers.InstantParceler
import com.caldeirasoft.outcast.data.db.typeconverters.InstantConverter
import com.caldeirasoft.outcast.domain.common.Constants
import com.caldeirasoft.outcast.domain.enums.PodcastFilter
import com.caldeirasoft.outcast.domain.enums.SortOrder
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.Artwork
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers

@Serializable
@Entity(
  tableName = "podcast",
  indices = [
    Index(value = ["podcastId"], unique = true)
  ]
)
data class Podcast(
  @PrimaryKey @ColumnInfo(name = "feedUrl") val feedUrl: String,
  @ColumnInfo(name = "name") val name: String,
  @ColumnInfo(name = "url") val url: String = "",
  @ColumnInfo(name = "podcastId") val podcastId: Long? = null,
  @ColumnInfo(name = "artistName") val artistName: String,
  @ColumnInfo(name = "artistId") val artistId: Long? = null,
  @ColumnInfo(name = "artistUrl") val artistUrl: String? = null,
  @ColumnInfo(name = "description") val description: String?,
  @ColumnInfo(name = "releaseDateTime") val releaseDateTime: Instant,
  @ColumnInfo(name = "artworkUrl") val artworkUrl: String,
  @ColumnInfo(name = "artworkDominantColor") val artworkDominantColor: String? = null,
  @ColumnInfo(name = "artworkTextColor") val artworkTextColor: String? = null,
  @ColumnInfo(name = "trackCount") val trackCount: Long = 0,
  @ColumnInfo(name = "podcastWebsiteURL") val podcastWebsiteURL: String? = null,
  @ColumnInfo(name = "copyright") val copyright: String? = null,
  @ColumnInfo(name = "userRating") val userRating: Double? = null,
  @ColumnInfo(name = "genre") val genre: String? = null,
  @ColumnInfo(name = "newFeedUrl") val newFeedUrl: String? = null,
  @ColumnInfo(name = "isComplete") val isComplete: Boolean = false,
  @ColumnInfo(name = "isExplicit") val isExplicit: Boolean = false,
  @ColumnInfo(name = "isFollowed") val isFollowed: Boolean = false,
  @ColumnInfo(name = "followedAt") val followedAt: Instant? = null,
  @ColumnInfo(name = "updatedAt") val updatedAt: Instant,
  @ColumnInfo(name = "podcast_filter") val podcastFilter: Int = PodcastFilter.ALL.ordinal,
  @ColumnInfo(name = "podcast_sort") val podcastSortOrder: Int = SortOrder.DESC.ordinal,
  ) {

  companion object {
    val Podcast.podcastSortOrderOption: SortOrder
      get() = SortOrder.values()[this.podcastSortOrder]

    val Podcast.podcastFilterOption: PodcastFilter
      get() = PodcastFilter.values()[this.podcastFilter]

    val Default = Podcast(
      feedUrl = "",
      name = "",
      artistName = "",
      artworkUrl = "",
      description = "",
      releaseDateTime = Instant.DISTANT_PAST,
      updatedAt = Instant.DISTANT_PAST,
    )

    fun StorePodcast.toPodcast() =
      Podcast(
        podcastId = id,
        name = name,
        url = url,
        artistName = artistName,
        artistId = artistId,
        artistUrl = artistUrl,
        description = description,
        feedUrl = feedUrl,
        releaseDateTime = releaseDateTime,
        artworkUrl = getArtworkUrl(),
        artworkDominantColor = artwork?.bgColor,
        artworkTextColor = artwork?.textColor1,
        trackCount = trackCount.toLong(),
        copyright = copyright,
        isExplicit = isExplicit,
        genre = genre?.name,
        newFeedUrl = null,
        isComplete = false,
        isFollowed = false,
        podcastWebsiteURL = podcastWebsiteUrl,
        userRating = userRating.toDouble(),
        updatedAt = Instant.DISTANT_PAST,
      )
  }
}
