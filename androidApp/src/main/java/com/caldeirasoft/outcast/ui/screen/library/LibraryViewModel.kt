package com.caldeirasoft.outcast.presentation.viewmodel

import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.usecase.LoadFollowedPodcastsUseCase
import com.caldeirasoft.outcast.ui.screen.library.LibraryViewState
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class LibraryViewModel(
    initialState: LibraryViewState,
) : MavericksViewModel<LibraryViewState>(initialState), KoinComponent
{
    private val loadFollowedPodcastsUseCase: LoadFollowedPodcastsUseCase by inject()

    init {
        loadFollowedPodcastsUseCase.invoke()
            .setOnEach { copy(podcasts = it) }
    }
}