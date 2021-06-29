@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.data.db.entities

import androidx.room.*
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
@Entity(tableName = "podcast_fts")
@Fts4(contentEntity = Podcast::class)
data class PodcastFTS(
  @ColumnInfo(name = "name") val name: String,
  @ColumnInfo(name = "artistName") val artistName: String,
)