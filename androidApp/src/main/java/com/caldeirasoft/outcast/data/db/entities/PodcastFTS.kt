@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.data.db.entities

import android.os.Parcelable
import androidx.room.*
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
@Entity(tableName = "podcast_fts")
@Fts4(contentEntity = Podcast::class)
data class PodcastFTS(
  @ColumnInfo(name = "name") val name: String,
  @ColumnInfo(name = "artistName") val artistName: String,
)