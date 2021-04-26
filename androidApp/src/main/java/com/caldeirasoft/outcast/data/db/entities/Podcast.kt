package com.caldeirasoft.outcast.data.db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.caldeirasoft.outcast.data.db.customparcelers.InstantParceler
import com.caldeirasoft.outcast.data.db.typeconverters.InstantConverter
import com.caldeirasoft.outcast.domain.common.Constants
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<Instant, InstantParceler>
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
  @ColumnInfo(name = "trackCount") val trackCount: Long = 0,
  @ColumnInfo(name = "podcastWebsiteURL") val podcastWebsiteURL: String? = null,
  @ColumnInfo(name = "copyright") val copyright: String? = null,
  @ColumnInfo(name = "userRating") val userRating: Double? = null,
  @ColumnInfo(name = "category") val category: Category? = null,
  @ColumnInfo(name = "newFeedUrl") val newFeedUrl: String? = null,
  @ColumnInfo(name = "isComplete") val isComplete: Boolean = false,
  @ColumnInfo(name = "isExplicit") val isExplicit: Boolean = false,
  @ColumnInfo(name = "isFollowed") val isFollowed: Boolean = false,
  @ColumnInfo(name = "updatedAt") val updatedAt: Instant
) : Parcelable {
  companion object {
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
        trackCount = trackCount.toLong(),
        copyright = copyright,
        isExplicit = isExplicit,
        category = category,
        newFeedUrl = null,
        isComplete = false,
        isFollowed = false,
        podcastWebsiteURL = podcastWebsiteUrl,
        userRating = userRating.toDouble(),
        updatedAt = Instant.DISTANT_PAST,
      )
  }
}
