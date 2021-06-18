package com.caldeirasoft.outcast.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search")
data class SearchEntity(
    @ColumnInfo(name = "query") val query: String,
) {
    @PrimaryKey
    var queryKey = hashCode()

    var timestamp = System.currentTimeMillis()
}