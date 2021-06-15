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
@Entity(tableName = "episode_fts")
@Fts4(contentEntity = Episode::class)
data class EpisodeFTS(
  @ColumnInfo(name = "name") val name: String,
  @ColumnInfo(name = "podcastName") val podcastName: String,
  @ColumnInfo(name = "artistName") val artistName: String,
)