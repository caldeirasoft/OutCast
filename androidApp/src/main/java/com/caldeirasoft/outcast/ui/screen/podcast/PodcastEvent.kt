package com.caldeirasoft.outcast.ui.screen.podcast

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.store.StoreData

sealed class PodcastEvent {
    data class OpenEpisodeDetail(val episode: Episode) : PodcastEvent()
    data class OpenStoreData(val storeData: StoreData) : PodcastEvent()
    data class OpenPodcastContextMenu(val podcast: Podcast) : PodcastEvent()
    object OpenSettings : PodcastEvent()
    data class OpenEpisodeContextMenu(val episode: Episode) : PodcastEvent()
}