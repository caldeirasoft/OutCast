package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.domain.repository.*
import com.caldeirasoft.outcast.presentation.viewmodel.InboxViewModel
import com.caldeirasoft.outcast.ui.screen.store.StoreViewModel
import com.caldeirasoft.outcast.ui.screen.storeroom.StoreCollectionViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


@ExperimentalCoroutinesApi
@FlowPreview
internal val appModule = module {
    single<DataStoreRepository> { DataStoreRepositoryImpl(context = get()) }

    viewModel { InboxViewModel(get()) }
    viewModel { StoreViewModel(
        fetchStoreDirectoryUseCase = get(),
        fetchStoreFrontUseCase = get(),
        getStoreItemsUseCase = get())
    }
    viewModel { (room: StoreRoom) -> StoreCollectionViewModel(
        getStoreItemsUseCase = get(),
        fetchStoreDataUseCase = get(),
        room = room,
    )}
}
