package com.caldeirasoft.outcast.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caldeirasoft.outcast.data.db.entities.SearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Query("SELECT `query` FROM search ORDER BY timestamp desc")
    suspend fun getSearches(): List<String>

    @Query("SELECT `query` FROM search WHERE `query` LIKE :searchQuery || '%' ORDER BY timestamp desc LIMIT 3")
    suspend fun searchHistory(searchQuery: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchEntity: SearchEntity)

    @Query("DELETE FROM search")
    suspend fun clearSearches()
}