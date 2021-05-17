package com.caldeirasoft.outcast.ui.screen.library

import com.caldeirasoft.outcast.data.db.entities.Podcast

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