package com.caldeirasoft.outcast.ui.screen.episode

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.EpisodeWithPodcast
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.store.StoreData

data class EpisodeViewState(
    val episode: Episode,
    val podcast: Podcast? = null,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)
{
    val artistData: StoreData? =
        podcast?.artistUrl?.let {
            StoreData(
                id = podcast.artistId ?: 0L,
                label = podcast.artistName,
                url = podcast.artistUrl.orEmpty(),
                storeFront = ""
            )
        }
}

sealed class EpisodeEvent {
    data class OpenPodcastDetail(val podcast: Podcast) : EpisodeEvent()
}