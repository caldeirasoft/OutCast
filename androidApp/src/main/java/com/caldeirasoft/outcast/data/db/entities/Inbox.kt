package com.caldeirasoft.outcast.data.db.entities

import androidx.room.*


@Entity(
  tableName = "inbox",
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
data class Inbox(
  @ColumnInfo(name = "feedUrl") val feedUrl: String,
  @ColumnInfo(name = "guid") val guid: String,
)