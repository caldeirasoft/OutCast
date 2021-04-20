package com.caldeirasoft.outcast.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.caldeirasoft.outcast.domain.models.Category
import kotlinx.datetime.Instant

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
  @ColumnInfo(name = "artistName") val artistName: String = "",
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
)
