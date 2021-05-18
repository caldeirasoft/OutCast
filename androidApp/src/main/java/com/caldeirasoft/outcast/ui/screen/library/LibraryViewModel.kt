package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.usecase.FetchFollowedPodcastsUseCase
import com.caldeirasoft.outcast.ui.screen.BaseViewModelEvents
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
) : BaseViewModelEvents<LibraryState, LibraryEvent>(
    initialState = LibraryState()
) {
    init {
        fetchFollowedPodcastsUseCase
            .getFollowedPodcasts()
            .setOnEach {
                copy(podcasts = it)
            }
    }


    fun changePodcastSort(sort: LibrarySort) {
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

    fun changePodcastSort(sortByOrder: Boolean) {
        viewModelScope.setState {
            copy(sortByDesc = sortByOrder)
        }
    }

    fun toggleDisplay() {
        viewModelScope.setState {
            copy(displayAsGrid = !displayAsGrid)
        }
    }
}