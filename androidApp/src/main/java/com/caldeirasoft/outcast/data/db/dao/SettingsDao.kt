package com.caldeirasoft.outcast.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.caldeirasoft.outcast.data.db.entities.Settings;

import kotlinx.coroutines.flow.Flow;

@Dao
interface SettingsDao  : EntityDao<Settings>{
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getAllSettings():Flow<Settings>
}