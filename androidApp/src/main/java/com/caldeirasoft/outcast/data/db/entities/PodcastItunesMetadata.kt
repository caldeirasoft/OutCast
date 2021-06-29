package com.caldeirasoft.outcast.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class PodcastItunesMetadata(
  @PrimaryKey @ColumnInfo(name = "feedUrl") val feedUrl: String,
  @ColumnInfo(name = "podcastId") val podcastId: Long? = null,
  @ColumnInfo(name = "artistId") val artistId: Long? = null,
  @ColumnInfo(name = "artistUrl") val artistUrl: String? = null,
  @ColumnInfo(name = "genre") val genre: String? = null,
  @ColumnInfo(name = "userRating") val userRating: Double? = null,
) {
  companion object {
    val Podcast.itunesMetaData: PodcastItunesMetadata
      get() = PodcastItunesMetadata(
        feedUrl = feedUrl,
        podcastId = podcastId,
        artistId = artistId,
        artistUrl = artistUrl,
        genre = genre,
        userRating = userRating,
      )
  }
}