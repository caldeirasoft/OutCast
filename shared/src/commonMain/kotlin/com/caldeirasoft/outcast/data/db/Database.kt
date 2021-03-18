package com.caldeirasoft.outcast.data.db

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.Genre
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

fun createDatabase(driver: SqlDriver): Database {
    val instantAdapter = object : ColumnAdapter<Instant, String> {
        override fun decode(databaseValue: String): Instant = Instant.parse(databaseValue)
        override fun encode(value: Instant): String = value.toString()
    }

    val artworkAdapter = object : ColumnAdapter<Artwork, String> {
        override fun decode(databaseValue: String): Artwork = Json.decodeFromString(Artwork.serializer(), databaseValue)
        override fun encode(value: Artwork): String =  Json.encodeToString(Artwork.serializer(), value)
    }

    val genreAdapter = object : ColumnAdapter<Genre, String> {
        override fun decode(databaseValue: String): Genre = Json.decodeFromString(Genre.serializer(), databaseValue)
        override fun encode(value: Genre): String =  Json.encodeToString(Genre.serializer(), value)
    }

    val genreListAdapter = object : ColumnAdapter<List<Int>, String> {
        override fun decode(databaseValue: String): List<Int> = databaseValue.split(',').map { it.toInt() }
        override fun encode(value: List<Int>): String =  value.joinToString()
    }

    val podcastAdapter = Podcast.Adapter(
        artworkAdapter = artworkAdapter,
        releaseDateTimeAdapter = instantAdapter,
        updatedAtAdapter = instantAdapter,
        genreAdapter = genreAdapter,
        newEpisodeActionAdapter = EnumColumnAdapter(),
    )

    val episodeAdapter = Episode.Adapter(
        artworkAdapter = artworkAdapter,
        releaseDateTimeAdapter = instantAdapter,
        updatedAtAdapter = instantAdapter,
        playedAtAdapter = instantAdapter,
        statusAdapter = EnumColumnAdapter()
    )


    val database = Database(driver,
        podcastAdapter = podcastAdapter,
        episodeAdapter = episodeAdapter,
    )
    return database
    // Do more work with the database (see below).
}
