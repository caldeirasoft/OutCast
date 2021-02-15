package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.db.createDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.dsl.module

actual val databaseModule = module {
    single<SqlDriver> { AndroidSqliteDriver(Database.Schema, get(), "outCastDb.db") }
    single { createDatabase(get()) }
}
