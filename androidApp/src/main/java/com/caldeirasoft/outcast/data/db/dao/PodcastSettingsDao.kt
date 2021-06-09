package com.caldeirasoft.outcast.data.db.dao;

import androidx.room.*
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.db.entities.PodcastSettings

import com.caldeirasoft.outcast.data.db.entities.Settings;
import com.caldeirasoft.outcast.domain.enums.SortOrder

import kotlinx.coroutines.flow.Flow;

@Dao
interface PodcastSettingsDao  : EntityDao<PodcastSettings>{
    @Transaction
    @Query("SELECT * FROM podcast_settings WHERE feedUrl = :feedUrl")
    fun getPodcastSettingsWithUrl(feedUrl: String): Flow<PodcastSettings?>
}