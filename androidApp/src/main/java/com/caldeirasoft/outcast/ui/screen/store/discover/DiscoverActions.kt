package com.caldeirasoft.outcast.ui.screen.store.discover

import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast

sealed class DiscoverActions {
    data class OpenPodcastDetail(val storePodcast: StorePodcast) : DiscoverActions()
    data class OpenEpisodeDetail(val storeEpisode: StoreEpisode) : DiscoverActions()
    data class OpenStoreData(val storeData: StoreData) : DiscoverActions()
    data class FollowPodcast(val storePodcast: StorePodcast) : DiscoverActions()
    object ClearNotificationNewVersionAvailable : DiscoverActions()
    object NavigateUp : DiscoverActions()
}