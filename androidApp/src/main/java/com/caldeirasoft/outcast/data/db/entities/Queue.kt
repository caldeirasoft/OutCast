package com.caldeirasoft.outcast.data.db.entities

import androidx.room.*


@Entity(
  tableName = "queue",
  primaryKeys = ["feedUrl", "guid"],
  foreignKeys = [
    ForeignKey(
      entity = Episode::class,
      parentColumns = ["feedUrl", "guid"],
      childColumns = ["feedUrl", "guid"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE
    )
  ],
)
data class Queue(
  @ColumnInfo(name = "feedUrl") val feedUrl: String,
  @ColumnInfo(name = "guid") val guid: String,
  @ColumnInfo(name = "queueIndex") val queueIndex: Int = 0
)