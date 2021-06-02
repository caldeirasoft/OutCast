@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.data.db.entities

import androidx.room.*
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.format.DateTimeFormatter

@Serializable
@Entity(
  tableName = Download.TABLE_NAME,
)
data class Download(
  @PrimaryKey @ColumnInfo(name = "url") val url: String,
  @ColumnInfo(name = "progress") val progress: Int = 0,
  @ColumnInfo(name = "state") val state: Int = 0,
  @ColumnInfo(name = "failure_reason") val failureReason: Int = 0,
  @ColumnInfo(name = "created_at") val createdAt: String? = null,
) {
  companion object {
    const val TABLE_NAME: String = "downloads"

    fun with(mediaUrl: String): Download = Download(
      url = mediaUrl,
      state = DownloadState.CREATED.ordinal,
      createdAt = Clock.System.now().toString()
    )

    val Download?.isCompleted: Boolean
      get() = this?.progress == 100 || this?.state == DownloadState.COMPLETED.ordinal

    val Download?.isInProgress: Boolean
      get() = this?.progress != 100 && this?.state == DownloadState.IN_PROGRESS.ordinal

    val Download?.isPaused: Boolean
      get() = this?.progress == DownloadState.PAUSED.ordinal
  }
}
