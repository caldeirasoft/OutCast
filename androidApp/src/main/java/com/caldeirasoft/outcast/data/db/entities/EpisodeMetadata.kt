package com.caldeirasoft.outcast.data.db.entities

import androidx.room.ColumnInfo
import com.caldeirasoft.outcast.domain.models.Category
import kotlinx.datetime.Instant

data class EpisodeMetadata(
  @ColumnInfo(name = "feedUrl") val feedUrl: String,
  @ColumnInfo(name = "guid") val guid: String,
  @ColumnInfo(name = "name") val name: String,
  @ColumnInfo(name = "podcastName") val podcastName: String,
  @ColumnInfo(name = "artistName") val artistName: String,
  @ColumnInfo(name = "releaseDateTime") val releaseDateTime: Instant,
  @ColumnInfo(name = "artworkUrl") val artworkUrl: String,
  @ColumnInfo(name = "mediaUrl") val mediaUrl: String,
  @ColumnInfo(name = "mediaType") val mediaType: String,
  @ColumnInfo(name = "duration") val duration: Int,
  @ColumnInfo(name = "category") val category: Category? = null,
  @ColumnInfo(name = "podcastEpisodeSeason") val podcastEpisodeSeason: Int? = null,
  @ColumnInfo(name = "podcastEpisodeNumber") val podcastEpisodeNumber: Int? = null,
  @ColumnInfo(name = "podcastEpisodeWebsiteUrl") val podcastEpisodeWebsiteUrl: String? = null,
  @ColumnInfo(name = "podcastEpisodeType") val podcastEpisodeType: String? = null,
  @ColumnInfo(name = "isExplicit") val isExplicit: Boolean = false,
) {
  companion object {
    val Episode.metadata: EpisodeMetadata
      get() = EpisodeMetadata(
        feedUrl = feedUrl,
        guid = guid,
        name = name,
        podcastName = podcastName,
        artistName = artistName,
        releaseDateTime = releaseDateTime,
        artworkUrl = artworkUrl,
        mediaUrl = mediaUrl,
        mediaType = mediaType,
        duration = duration,
        category = category,
        podcastEpisodeSeason = podcastEpisodeSeason,
        podcastEpisodeNumber = podcastEpisodeNumber,
        podcastEpisodeWebsiteUrl = podcastEpisodeWebsiteUrl,
        podcastEpisodeType = podcastEpisodeType,
        isExplicit = isExplicit
      )
  }
}