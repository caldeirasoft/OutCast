package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.db.createDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.dsl.module

actual val databaseModule = module {
    single<SqlDriver> {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        driver
    }
    single { createDatabase(get()) }
}
