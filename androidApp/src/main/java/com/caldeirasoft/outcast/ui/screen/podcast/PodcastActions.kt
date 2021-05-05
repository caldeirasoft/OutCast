package com.caldeirasoft.outcast.ui.screen.podcast

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePodcast

sealed class PodcastActions {
    data class SetPodcast(val storePodcast: StorePodcast): PodcastActions()
    data class OpenEpisodeDetail(val episode: Episode) : PodcastActions()
    data class OpenStoreDataDetail(val storeData: StoreData) : PodcastActions()
    data class OpenCategoryDataDetail(val category: Category) : PodcastActions()
    object FollowPodcast : PodcastActions()
    object UnfollowPodcast : PodcastActions()
    object ShowAllEpisodes : PodcastActions()
    object NavigateUp : PodcastActions()
    object OpenPodcastContextMenu : PodcastActions()
    data class OpenEpisodeContextMenu(val episode: Episode) : PodcastActions()
    object OpenSettings : PodcastActions()
}