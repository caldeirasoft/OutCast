package com.caldeirasoft.outcast.ui.screen.store.search

import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class StoreSearchViewModel(
    initialState: StoreSearchViewState,
) : MavericksViewModel<StoreSearchViewState>(initialState), KoinComponent {

    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    // get genres
    suspend fun getGenres() =
        suspend {
            val storeFront = fetchStoreFrontUseCase.getStoreFront().first()
            //loadStoreGenreDataUseCase.execute(storeFront = storeFront)
        }
}