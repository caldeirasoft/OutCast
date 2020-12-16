package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.StoreDirectory
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDirectoryUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.base.StoreRoomBaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@ExperimentalCoroutinesApi
class StoreDirectoryViewModel : StoreRoomBaseViewModel<StoreDirectory>(), KoinComponent {
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()
    private val storeFront = fetchStoreFrontUseCase.getStoreFront()

    val state = storeDataState

    val genres: StateFlow<List<StoreGenre>> =
        storeData
            .filterNotNull()
            .mapNotNull { it.genres }
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    override fun getPagingConfig() = PagingConfig(
        pageSize = 4,
        enablePlaceholders = false,
        maxSize = 100,
        prefetchDistance = 2
    )

    init {
        storeFront
            .flatMapLatest { fetchStoreDirectoryUseCase.execute(it) }
            .onEach { storeResourceData.emit(it) }
            .launchIn(viewModelScope)
    }
}