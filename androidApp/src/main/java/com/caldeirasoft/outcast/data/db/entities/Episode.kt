package com.caldeirasoft.outcast.data.db.entities

import androidx.room.*
import kotlinx.datetime.Instant


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
  @ColumnInfo(name = "podcastEpisodeSeason") val podcastEpisodeSeason: Int? = null,
  @ColumnInfo(name = "podcastEpisodeNumber") val podcastEpisodeNumber: Int? = null,
  @ColumnInfo(name = "podcastEpisodeWebsiteUrl") val podcastEpisodeWebsiteUrl: String? = null,
  @ColumnInfo(name = "podcastEpisodeType") val podcastEpisodeType: String? = null,
  @ColumnInfo(name = "playbackPosition") val playbackPosition: Int? = null,
  @ColumnInfo(name = "isExplicit") val isExplicit: Boolean = false,
  @ColumnInfo(name = "isPlayed") val isPlayed: Boolean = false,
  @ColumnInfo(name = "isFavorite") val isFavorite: Boolean = false,
  @ColumnInfo(name = "playedAt") val playedAt: Instant? = null,
  @ColumnInfo(name = "updatedAt") val updatedAt: Instant
)

