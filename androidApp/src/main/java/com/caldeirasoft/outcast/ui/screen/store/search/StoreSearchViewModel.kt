package com.caldeirasoft.outcast.ui.screen.store.search

import androidx.lifecycle.ViewModel
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import kotlinx.coroutines.flow.first

class StoreSearchViewModel(
    initialState: StoreSearchViewState,
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase,
) : ViewModel() {
    // get genres
    suspend fun getGenres() =
        suspend {
            val storeFront = fetchStoreFrontUseCase.getStoreFront().first()
            //loadStoreGenreDataUseCase.execute(storeFront = storeFront)
        }
}