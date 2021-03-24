package com.caldeirasoft.outcast.ui.screen.store.genre

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreGroupingPagingDataUseCase
import com.caldeirasoft.outcast.ui.screen.store.base.FollowViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class StoreGenreViewModel(
    initialState: StoreGenreViewState,
) : FollowViewModel<StoreGenreViewState>(initialState), KoinComponent {

    private val fetchStoreGroupingPagingDataUseCase: FetchStoreGroupingPagingDataUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    init {
        followingStatus.setOnEach { copy(followingStatus = it) }
    }

    // paged list
    @OptIn(FlowPreview::class)
    val discover: Flow<PagingData<StoreItem>> by lazy {
        fetchStoreFrontUseCase.getStoreFront()
            .map { storeFront ->
                fetchStoreGroupingPagingDataUseCase.executeAsync(
                    scope = viewModelScope,
                    genre = initialState.genreId,
                    storeFront = storeFront,
                    dataLoadedCallback = { })
            }
            .flattenMerge()
            .cachedIn(viewModelScope)
    }
}