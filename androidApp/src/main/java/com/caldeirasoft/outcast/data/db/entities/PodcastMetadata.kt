package com.caldeirasoft.outcast.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.Artwork
import kotlinx.datetime.Instant

data class PodcastMetadata(
  @PrimaryKey @ColumnInfo(name = "feedUrl") val feedUrl: String,
  @ColumnInfo(name = "name") val name: String,
  @ColumnInfo(name = "url") val url: String = "",
  @ColumnInfo(name = "artistName") val artistName: String = "",
  @ColumnInfo(name = "description") val description: String?,
  @ColumnInfo(name = "releaseDateTime") val releaseDateTime: Instant,
  @ColumnInfo(name = "artworkUrl") val artworkUrl: String,
  @ColumnInfo(name = "trackCount") val trackCount: Long = 0,
  @ColumnInfo(name = "podcastWebsiteURL") val podcastWebsiteURL: String? = null,
  @ColumnInfo(name = "copyright") val copyright: String? = null,
  //@ColumnInfo(name = "category") val category: Category? = null,
  @ColumnInfo(name = "newFeedUrl") val newFeedUrl: String? = null,
  @ColumnInfo(name = "isComplete") val isComplete: Boolean = false,
  @ColumnInfo(name = "isExplicit") val isExplicit: Boolean = false,
  @ColumnInfo(name = "updatedAt") val updatedAt: Instant
) {
  companion object {
    val Podcast.metaData: PodcastMetadata
      get() = PodcastMetadata(
        feedUrl = feedUrl,
        name = name,
        url = url,
        artistName = artistName,
        description = description,
        releaseDateTime = releaseDateTime,
        artworkUrl = artworkUrl,
        trackCount = trackCount,
        podcastWebsiteURL = podcastWebsiteURL,
        copyright = copyright,
        newFeedUrl = newFeedUrl,
        isComplete = isComplete,
        isExplicit = isExplicit,
        updatedAt = updatedAt
      )
  }
}