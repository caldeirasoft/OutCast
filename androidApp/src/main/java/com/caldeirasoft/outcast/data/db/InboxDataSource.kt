package com.caldeirasoft.outcast.data.db

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.db.Episode
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow

class InboxDataSource (val database: Database) {
    fun fetchEpisodes(): Flow<List<Episode>> =
        database.inboxQueries
            .selectAll()
            .asFlow()
            .mapToList()

    fun fetchEpisodesByGenre(genreId: Int): Flow<List<Episode>> =
        database.inboxQueries
            .selectEpisodesByGenreId(genreId = genreId)
            .asFlow()
            .mapToList()


    fun fetchGenreIds(): Flow<List<Int>> =
        database.inboxQueries
            .selectGenreId(mapper = { genreId: Int? -> genreId ?: 0 })
            .asFlow()
            .mapToList()

    fun addToInbox(episode: Episode) {
        database.inboxQueries
            .addToInbox(episodeId = episode.episodeId)
    }

    fun removeFromInbox(episodeId: Long) {
        database.inboxQueries
            .removeFromInbox(episodeId = episodeId)
    }

    fun deleteAll() {
        TODO("Not yet implemented")
    }
}
