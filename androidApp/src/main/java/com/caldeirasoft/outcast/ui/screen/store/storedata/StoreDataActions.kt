package com.caldeirasoft.outcast.ui.screen.store.storedata

import com.caldeirasoft.outcast.domain.models.store.StoreCategory
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast

sealed class StoreDataActions {
    data class OpenPodcastDetail(val storePodcast: StorePodcast) : StoreDataActions()
    data class OpenEpisodeDetail(val storeEpisode: StoreEpisode) : StoreDataActions()
    data class OpenStoreData(val storeData: StoreData) : StoreDataActions()
    data class FollowPodcast(val storePodcast: StorePodcast) : StoreDataActions()
    object OpenCategories : StoreDataActions()
    data class SelectCategory(val category: StoreCategory): StoreDataActions()
    object ClearNotificationNewVersionAvailable : StoreDataActions()
    object NavigateUp : StoreDataActions()
}