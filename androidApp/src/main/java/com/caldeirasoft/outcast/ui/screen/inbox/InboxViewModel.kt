package com.caldeirasoft.outcast.presentation.viewmodel

import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.usecase.FetchInboxUseCase
import com.caldeirasoft.outcast.ui.screen.inbox.InboxViewState
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class InboxViewModel(
    initialState: InboxViewState,
) : MavericksViewModel<InboxViewState>(initialState), KoinComponent {
    private val fetchInboxUseCase: FetchInboxUseCase by inject()

    init {
        fetchInboxUseCase.invoke()
            .setOnEach { copy(episodes = it) }
    }
}