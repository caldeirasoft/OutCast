package com.caldeirasoft.outcast.ui.screen.podcast

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.ui.screen.episodes.EpisodesEvent

sealed class PodcastEvent : EpisodesEvent() {
    data class OpenEpisodeDetail(val episode: Episode) : PodcastEvent()
    data class OpenStoreData(val storeData: StoreData) : PodcastEvent()
    object OpenSettings : PodcastEvent()
    data class SharePodcast(val podcast: Podcast) : PodcastEvent()
    data class OpenWebsite(val websiteUrl: String) : PodcastEvent()
    object ToggleNotifications : PodcastEvent()
}