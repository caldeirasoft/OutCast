package com.caldeirasoft.outcast.ui.screen.store.discover

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStorePagingDataUseCase
import com.caldeirasoft.outcast.ui.screen.store.base.FollowViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

@OptIn(KoinApiExtension::class)
@ExperimentalCoroutinesApi
class DiscoverViewModel(
    initialState: DiscoverState,
) : FollowViewModel<DiscoverState>(initialState), KoinComponent {

    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()
    private val loadStorePagingDataUseCase: LoadStorePagingDataUseCase by inject()

    init {
        followingStatus.setOnEach { copy(followingStatus = it) }
    }

    // paged list
    @OptIn(FlowPreview::class)
    val discover: Flow<PagingData<StoreItem>> =
        fetchStoreFrontUseCase.getStoreFront()
            .map { storeFront ->
                loadStorePagingDataUseCase.executeAsync(
                    scope = viewModelScope,
                    storeData = initialState.storeData,
                    storeFront = storeFront,
                    newVersionAvailable = { Timber.d("DBG - New version available") },
                    dataLoadedCallback = { page ->
                        setState {
                            copy(storePage = page, title = page.label)
                        }
                    })
            }
            .flattenMerge()
            .cachedIn(viewModelScope)
}