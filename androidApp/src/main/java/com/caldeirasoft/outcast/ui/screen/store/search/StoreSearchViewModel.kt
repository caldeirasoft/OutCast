package com.caldeirasoft.outcast.ui.screen.store.search

import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import kotlinx.coroutines.flow.first

class StoreSearchViewModel(
    initialState: StoreSearchViewState,
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase,
) : MavericksViewModel<StoreSearchViewState>(initialState) {
    // get genres
    suspend fun getGenres() =
        suspend {
            val storeFront = fetchStoreFrontUseCase.getStoreFront().first()
            //loadStoreGenreDataUseCase.execute(storeFront = storeFront)
        }
}