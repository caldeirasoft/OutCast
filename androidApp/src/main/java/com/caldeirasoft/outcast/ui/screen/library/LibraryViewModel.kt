package com.caldeirasoft.outcast.presentation.viewmodel

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.hiltMavericksViewModelFactory
import com.caldeirasoft.outcast.domain.usecase.LoadFollowedPodcastsUseCase
import com.caldeirasoft.outcast.ui.screen.library.LibraryViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class LibraryViewModel @AssistedInject constructor(
    @Assisted initialState: LibraryViewState,
    val podcastDao: PodcastDao,
) : MavericksViewModel<LibraryViewState>(initialState) {
    init {
        podcastDao.getFollowedPodcasts()
            .setOnEach { copy(podcasts = it) }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<LibraryViewModel, LibraryViewState> {
        override fun create(initialState: LibraryViewState): LibraryViewModel
    }

    companion object :
        MavericksViewModelFactory<LibraryViewModel, LibraryViewState> by hiltMavericksViewModelFactory()
}