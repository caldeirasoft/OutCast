package com.caldeirasoft.outcast.presentation.viewmodel

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.hiltMavericksViewModelFactory
import com.caldeirasoft.outcast.domain.usecase.FetchInboxUseCase
import com.caldeirasoft.outcast.ui.screen.inbox.InboxViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InboxViewModel @AssistedInject constructor(
    @Assisted initialState: InboxViewState,
    private val fetchInboxUseCase: FetchInboxUseCase,
) : MavericksViewModel<InboxViewState>(initialState) {


    init {
        fetchInboxUseCase.invoke()
            .setOnEach { copy(episodes = it) }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<InboxViewModel, InboxViewState> {
        override fun create(initialState: InboxViewState): InboxViewModel
    }

    companion object :
        MavericksViewModelFactory<InboxViewModel, InboxViewState> by hiltMavericksViewModelFactory()
}