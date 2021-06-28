package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.ui.screen.base.BaseViewModel
import com.caldeirasoft.outcast.ui.screen.library.LibrarySort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val podcastsRepository: PodcastsRepository,
) : BaseViewModel<LibraryViewModel.State, LibraryViewModel.Event, LibraryViewModel.Action>(
    initialState = State()
) {
    override fun activate() {
        podcastsRepository
            .getFollowedPodcasts()
            .setOnEach {
                copy(podcasts = it)
            }
    }

    override suspend fun performAction(action: Action) = when(action) {
        is Action.OpenPodcastDetail -> emitEvent(Event.OpenPodcastDetail(action.podcast))
        is Action.OpenPlayedEpisodes -> emitEvent(Event.OpenPlayedEpisodes)
        is Action.OpenSavedEpisodes -> emitEvent(Event.OpenSavedEpisodes)
        is Action.OpenSideLoads -> Unit
        is Action.OpenSettings -> emitEvent(Event.OpenSettings)
        is Action.ToggleDisplay -> toggleDisplay()
        is Action.ChangeSort -> changePodcastSort(action.sort)
        is Action.ChangeSortOrder -> changePodcastSort(action.sortByDesc)
        else -> Unit
    }

    private fun changePodcastSort(sort: LibrarySort) {
        when(sort) {
            LibrarySort.RECENTLY_UPDATED, LibrarySort.RECENTLY_FOLLOWED ->
                viewModelScope.setState {
                    copy(sortBy = sort, sortByDesc = true)
                }
            LibrarySort.NAME, LibrarySort.AUTHOR ->
                viewModelScope.setState {
                    copy(sortBy = sort, sortByDesc = false)
                }
        }
    }

    private fun changePodcastSort(sortByOrder: Boolean) {
        viewModelScope.setState {
            copy(sortByDesc = sortByOrder)
        }
    }

    private fun toggleDisplay() {
        viewModelScope.setState {
            copy(displayAsGrid = !displayAsGrid)
        }
    }

    data class State(
        val podcasts: List<Podcast> = emptyList(),
        val sortBy: LibrarySort = LibrarySort.RECENTLY_UPDATED,
        val sortByDesc: Boolean = false,
        val displayAsGrid: Boolean = false,
        val savedEpisodesCount: Int = 0,
    ) {
        val sortedPodcasts: List<Podcast>
            get() = when(sortBy) {
                LibrarySort.NAME -> podcasts.sortedBy { it.name }
                LibrarySort.AUTHOR -> podcasts.sortedBy { it.artistName }
                LibrarySort.RECENTLY_FOLLOWED -> podcasts.sortedBy { it.updatedAt.epochSeconds }
                LibrarySort.RECENTLY_UPDATED -> podcasts.sortedBy { it.updatedAt.epochSeconds }
            }.let {
                if (sortByDesc)
                    it.reversed()
                else it
            }

        val newEpisodesUpdatedAt: Instant?
            get() = podcasts.maxOfOrNull { it.releaseDateTime }
    }

    sealed class Event {
        data class OpenPodcastDetail(val podcast: Podcast) : Event()
        object OpenSavedEpisodes : Event()
        object OpenPlayedEpisodes : Event()
        object OpenSideLoads : Event()
        object OpenSettings : Event()
        data class OpenPodcastContextMenu(val podcast: Podcast) : Event()
        data class OpenSortByBottomSheet(val sort: LibrarySort, val sortByDesc: Boolean) : Event()
        object Exit: Event()
    }

    sealed class Action {
        data class OpenPodcastDetail(val podcast: Podcast) : Action()
        object OpenSavedEpisodes : Action()
        object OpenPlayedEpisodes : Action()
        object OpenSideLoads : Action()
        object OpenSettings : Action()
        data class ChangeSort(val sort: LibrarySort) : Action()
        data class ChangeSortOrder(val sortByDesc: Boolean) : Action()
        object ToggleDisplay : Action()
    }
}