package com.caldeirasoft.outcast.presentation.viewmodel

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.caldeirasoft.outcast.data.db.dao.InboxDao
import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.hiltMavericksViewModelFactory
import com.caldeirasoft.outcast.ui.screen.inbox.InboxViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InboxViewModel @AssistedInject constructor(
    @Assisted initialState: InboxViewState,
    inboxDao: InboxDao
) : MavericksViewModel<InboxViewState>(initialState) {


    init {
        inboxDao.getEpisodes()
            .setOnEach { copy(episodes = it) }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<InboxViewModel, InboxViewState> {
        override fun create(initialState: InboxViewState): InboxViewModel
    }

    companion object :
        MavericksViewModelFactory<InboxViewModel, InboxViewState> by hiltMavericksViewModelFactory()
}