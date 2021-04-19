package com.caldeirasoft.outcast.ui.screen.store.search

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.caldeirasoft.outcast.domain.models.Category

data class StoreSearchViewState(
    val storeGenreData: Async<Category> = Uninitialized,
) : MavericksState
