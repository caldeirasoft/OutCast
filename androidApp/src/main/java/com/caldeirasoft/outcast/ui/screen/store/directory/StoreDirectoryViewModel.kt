package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStoreDirectoryPagingDataUseCase
import com.caldeirasoft.outcast.domain.util.tryCast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

@OptIn(KoinApiExtension::class)
@FlowPreview
@ExperimentalCoroutinesApi
class StoreDirectoryViewModel(
    initialState: StoreDirectoryViewState,
) : MavericksViewModel<StoreDirectoryViewState>(initialState), KoinComponent, ScrollViewModel {

    private val loadStoreDirectoryPagingDataUseCase: LoadStoreDirectoryPagingDataUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()
    override var scrollState: ListState = ListState()

    // paged list
    val discover: Flow<PagingData<StoreItem>> =
        getStoreDataPagedList()
            .cachedIn(viewModelScope)


    // get paged list
    private fun getStoreDataPagedList(): Flow<PagingData<StoreItem>> =
        fetchStoreFrontUseCase.getStoreFront()
            .onEach {
                setState { copy(storeFront = it) }
            }
            .flatMapConcat { store ->
                loadStoreDirectoryPagingDataUseCase.executeAsync(
                    scope = viewModelScope,
                    storeFront = store,
                    newVersionAvailable = { Timber.d("DBG - New version available") },
                    dataLoadedCallback = {
                        it.tryCast<StoreGroupingPage> {
                            //this@StoreDirectoryViewModel.storeData.tryEmit(this)
                        }
                    })
            }
}