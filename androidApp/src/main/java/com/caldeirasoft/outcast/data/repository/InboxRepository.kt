package com.caldeirasoft.outcast.data.repository

import kotlinx.coroutines.flow.Flow

/*
class InboxRepository(val database: Database) {
    fun fetchEpisodes(): Flow<List<Episode>> =
        database.inboxQueries
            .selectAll()
            .asFlow()
            .mapToList()

    fun addToInbox(episode: Episode) {
        database.inboxQueries
            .addToInbox(feedUrl = episode.feedUrl, guid = episode.guid)
    }

    fun removeFromInbox(episode: Episode) {
        database.inboxQueries
            .removeFromInbox(feedUrl = episode.feedUrl, guid = episode.guid)
    }

    fun deleteAll() {
        TODO("Not yet implemented")
    }
}
*/
