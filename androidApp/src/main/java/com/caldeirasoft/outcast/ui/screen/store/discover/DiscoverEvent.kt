package com.caldeirasoft.outcast.ui.screen.store.discover

import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast

sealed class DiscoverEvent {
    data class OpenPodcastDetail(val storePodcast: StorePodcast) : DiscoverEvent()
    data class OpenEpisodeDetail(val storeEpisode: StoreEpisode) : DiscoverEvent()
    data class OpenStoreData(val storeData: StoreData) : DiscoverEvent()
}