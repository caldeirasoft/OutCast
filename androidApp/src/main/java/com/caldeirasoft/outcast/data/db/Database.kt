package com.caldeirasoft.outcast.data.db

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.Category
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.datetime.Instant

fun createDatabase(driver: SqlDriver): Database {
    val instantAdapter = object : ColumnAdapter<Instant, String> {
        override fun decode(databaseValue: String): Instant = Instant.parse(databaseValue)
        override fun encode(value: Instant): String = value.toString()
    }

    val categoryAdapter = object : ColumnAdapter<Category, String> {
        override fun decode(databaseValue: String): Category = Category.valueOf(databaseValue)
        override fun encode(value: Category): String = value.name
    }

    val podcastAdapter = Podcast.Adapter(
        releaseDateTimeAdapter = instantAdapter,
        updatedAtAdapter = instantAdapter,
        categoryAdapter = categoryAdapter,
        newEpisodeActionAdapter = EnumColumnAdapter(),
    )

    val episodeAdapter = Episode.Adapter(
        releaseDateTimeAdapter = instantAdapter,
        updatedAtAdapter = instantAdapter,
        playedAtAdapter = instantAdapter,
    )


    val database = Database(driver,
        podcastAdapter = podcastAdapter,
        episodeAdapter = episodeAdapter,
    )
    return database
    // Do more work with the database (see below).
}
