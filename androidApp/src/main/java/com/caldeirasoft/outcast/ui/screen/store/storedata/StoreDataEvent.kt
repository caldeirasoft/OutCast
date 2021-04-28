package com.caldeirasoft.outcast.ui.screen.store.storedata

import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast

sealed class StoreDataEvent {
    data class OpenPodcastDetail(val storePodcast: StorePodcast) : StoreDataEvent()
    data class OpenEpisodeDetail(val storeEpisode: StoreEpisode) : StoreDataEvent()
    data class OpenStoreData(val storeData: StoreData) : StoreDataEvent()
}