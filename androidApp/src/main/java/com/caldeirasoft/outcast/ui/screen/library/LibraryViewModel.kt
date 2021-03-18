package com.caldeirasoft.outcast.presentation.viewmodel

import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.usecase.FetchPodcastsSubscribedUseCase
import com.caldeirasoft.outcast.ui.screen.library.LibraryViewState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LibraryViewModel(
    initialState: LibraryViewState,
) : MavericksViewModel<LibraryViewState>(initialState), KoinComponent
{
    private val fetchPodcastsSubscribedUseCase: FetchPodcastsSubscribedUseCase by inject()

    init {
        fetchPodcastsSubscribedUseCase.invoke()
            .setOnEach { copy(podcasts = it) }
    }
}