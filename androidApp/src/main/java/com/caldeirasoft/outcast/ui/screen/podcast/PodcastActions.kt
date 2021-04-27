package com.caldeirasoft.outcast.ui.screen.podcast

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.store.discover.DiscoverActions

sealed class PodcastActions {
    data class OpenEpisodeDetail(val episode: Episode) : PodcastActions()
    data class OpenStoreDataDetail(val storeData: StoreData) : PodcastActions()
    data class OpenCategoryDataDetail(val category: Category) : PodcastActions()
    object FollowPodcast : PodcastActions()
    object UnfollowPodcast : PodcastActions()
    object ShowAllEpisodes : PodcastActions()
    object NavigateUp : PodcastActions()
}