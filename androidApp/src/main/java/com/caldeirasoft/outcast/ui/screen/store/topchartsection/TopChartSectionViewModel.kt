package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModelFactory
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.hiltMavericksViewModelFactory
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FollowUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStoreTopChartsPagingDataUseCase
import com.caldeirasoft.outcast.ui.screen.store.base.FollowViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class TopChartSectionViewModel @AssistedInject constructor(
    @Assisted initialState: TopChartSectionState,
    followUseCase: FollowUseCase,
    private val loadStoreTopChartsPagingDataUseCase: LoadStoreTopChartsPagingDataUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    podcastDao: PodcastDao
) : FollowViewModel<TopChartSectionState>(
    initialState,
    followUseCase,
    podcastDao
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
        setState {
            copy(category = category)
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<TopChartSectionViewModel, TopChartSectionState> {
        override fun create(initialState: TopChartSectionState): TopChartSectionViewModel
    }

    companion object :
        MavericksViewModelFactory<TopChartSectionViewModel, TopChartSectionState> by hiltMavericksViewModelFactory()
}


