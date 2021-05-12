package com.caldeirasoft.outcast.ui.screen.library

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastActions

sealed class LibraryActions {
    data class OpenPodcastDetail(val podcast: Podcast) : LibraryActions()
    object OpenLatestEpisodesScreen : LibraryActions()
    object OpenSavedEpisodesScreen : LibraryActions()
    object OpenPlayedEpisodesScreen : LibraryActions()
    object ToggleDisplay : LibraryActions()
    object OpenSortByBottomSheet : LibraryActions()
    data class ChangeSort(val sortBy: LibrarySort) : LibraryActions()
    data class ChangeSortOrder(val sortByDesc: Boolean) : LibraryActions()
    object AddPodcastToLibrary : LibraryActions()
    data class SharePodcast(val podcast: Podcast): LibraryActions()
    data class UnfollowPodcast(val podcast: Podcast): LibraryActions()
    object NavigateUp : LibraryActions()
}