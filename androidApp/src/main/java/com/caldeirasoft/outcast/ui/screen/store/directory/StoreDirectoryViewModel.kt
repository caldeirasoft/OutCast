package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import com.caldeirasoft.outcast.domain.usecase.LoadStoreDirectoryPagingDataUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.util.tryCast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
class StoreDirectoryViewModel(
    private val loadStoreDirectoryPagingDataUseCase: LoadStoreDirectoryPagingDataUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
) : ViewModel() {
    // storefront
    private val storeFront: StateFlow<String?> =
        fetchStoreFrontUseCase.getStoreFront()
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // store data
    protected val storeData = MutableStateFlow<StoreGroupingPage?>(null)

    // paged list
    val discover: Flow<PagingData<StoreItem>> =
        getStoreDataPagedList()
            .cachedIn(viewModelScope)

    // state
    val state: StateFlow<State> =
        storeFront
            .filterNotNull()
            .combine(storeData) { storeFront, storeData ->
                State(storeFront = storeFront, storeData = storeData)
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, State())

    private fun getStoreDataPagedList(): Flow<PagingData<StoreItem>> =
        storeFront
            .filterNotNull()
            .flatMapLatest {
                Timber.d("DBG - getStoreDataPagedList")
                loadStoreDirectoryPagingDataUseCase.executeAsync(
                    scope = viewModelScope,
                    storeFront = it,
                    newVersionAvailable = {
                        Timber.d("DBG - New version available")
                    },
                    dataLoadedCallback = {
                        it.tryCast<StoreGroupingPage> {
                            this@StoreDirectoryViewModel.storeData.tryEmit(this)
                        }
                    }
                )
            }

    data class State(
        val storeData: StoreGroupingPage? = null,
        val storeFront: String? = null,
    )
}