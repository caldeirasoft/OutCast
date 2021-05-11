package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.usecase.FetchFollowedPodcastsUseCase
import com.caldeirasoft.outcast.ui.screen.MvieViewModel
import com.caldeirasoft.outcast.ui.screen.library.LibraryActions
import com.caldeirasoft.outcast.ui.screen.library.LibraryEvent
import com.caldeirasoft.outcast.ui.screen.library.LibrarySort
import com.caldeirasoft.outcast.ui.screen.library.LibraryState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fetchFollowedPodcastsUseCase: FetchFollowedPodcastsUseCase,
) : MvieViewModel<LibraryState, LibraryEvent, LibraryActions>(
    initialState = LibraryState()
) {
    init {
        fetchFollowedPodcastsUseCase
            .getFollowedPodcasts()
            .setOnEach {
                copy(podcasts = it)
            }
    }

    override suspend fun performAction(action: LibraryActions) = when (action) {
        is LibraryActions.OpenSortByBottomSheet -> withState {
            emitEvent(LibraryEvent.OpenSortByBottomSheet(it.sortBy, it.sortByDesc))
        }
        is LibraryActions.ChangeSort -> changePodcastSort(action.sortBy)
        is LibraryActions.ChangeSortOrder -> changePodcastSort(action.sortByDesc)
        is LibraryActions.ToggleDisplay -> toggleDisplay()
        else -> Unit
    }

    private suspend fun changePodcastSort(sort: LibrarySort) {
        when(sort) {
            LibrarySort.RECENTLY_UPDATED, LibrarySort.RECENTLY_FOLLOWED ->
                setState {
                    copy(sortBy = sort, sortByDesc = true)
                }
            LibrarySort.NAME, LibrarySort.AUTHOR ->
                setState {
                    copy(sortBy = sort, sortByDesc = false)
                }
        }
    }

    private suspend fun changePodcastSort(sortByOrder: Boolean) {
        setState {
            copy(sortByDesc = sortByOrder)
        }
    }

    private suspend fun toggleDisplay() {
        setState {
            copy(displayAsGrid = !displayAsGrid)
        }
    }
}