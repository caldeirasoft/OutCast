package com.caldeirasoft.outcast.di

import android.content.Context
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.db.createDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): Database =
        createDatabase(AndroidSqliteDriver(Database.Schema, appContext, "outCastDb.db"))
}
