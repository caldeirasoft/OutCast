package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.usecase.FetchFollowedPodcastsUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FollowUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStoreTopChartsPagingDataUseCase
import com.caldeirasoft.outcast.ui.screen.store.base.FollowViewModel
import com.caldeirasoft.outcast.ui.screen.store.storedata.StoreDataActions
import com.caldeirasoft.outcast.ui.screen.store.storedata.StoreDataEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TopChartSectionViewModel @Inject constructor(
    followUseCase: FollowUseCase,
    private val loadStoreTopChartsPagingDataUseCase: LoadStoreTopChartsPagingDataUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    fetchFollowedPodcastsUseCase: FetchFollowedPodcastsUseCase,
) : FollowViewModel<TopChartSectionState, StoreDataEvent, StoreDataActions>(
    TopChartSectionState(StoreItemType.PODCAST),
    followUseCase,
    fetchFollowedPodcastsUseCase
) {

    init {
        followingStatus.setOnEach { copy(followingStatus = it) }
        followLoadingStatus.setOnEach { copy(followLoadingStatus = it) }
    }

    // paged list
    @OptIn(FlowPreview::class)
    val topCharts: Flow<PagingData<StoreItem>> =
        fetchStoreFrontUseCase.getStoreFront()
            .map { storeFront ->
                loadStoreTopChartsPagingDataUseCase.execute(
                    scope = viewModelScope,
                    genreId = null, // genre
                    storeFront = storeFront,
                    storeItemType = initialState.storeItemType, // item type
                )
            }
            .flattenMerge()
            .cachedIn(viewModelScope)

    fun onCategorySelected(category: Category?) {
        viewModelScope.setState {
            copy(category = category)
        }
    }

    override suspend fun performAction(action: StoreDataActions) {
        TODO("Not yet implemented")
    }
}


