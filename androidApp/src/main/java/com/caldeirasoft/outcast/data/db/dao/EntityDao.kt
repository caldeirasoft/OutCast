package com.caldeirasoft.outcast.data.db.dao

import androidx.room.*

@Dao
interface EntityDao<in E> {
    @Insert
    fun insert(entity: E): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg entity: E): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(obj: List<E>): List<Long>

    @Update
    suspend fun update(entity: E)

    @Delete
    suspend fun delete(entity: E): Int
}