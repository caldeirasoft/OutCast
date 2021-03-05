package com.caldeirasoft.outcast.ui.screen.store.genre

import androidx.paging.PagingData
import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage

data class StoreGenreViewState(
    val genreId: Int,
    val storeFront: String,
    val title: String,
    val storeData: StoreGroupingPage? = null,
    val discover: PagingData<StoreItem> = PagingData.empty(),
) : MavericksState {
    constructor(storeGenre: StoreGenre) :
            this(genreId = storeGenre.id,
                storeFront = storeGenre.storeFront,
                title = storeGenre.name)
}