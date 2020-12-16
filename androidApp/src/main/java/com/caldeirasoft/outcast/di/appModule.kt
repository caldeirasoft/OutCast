package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.domain.repository.*
import com.caldeirasoft.outcast.presentation.viewmodel.InboxViewModel
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreDirectoryViewModel
import com.caldeirasoft.outcast.ui.screen.store.storeroom.StoreRoomViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


@ExperimentalCoroutinesApi
@FlowPreview
internal val appModule = module {
    single<DataStoreRepository> { DataStoreRepositoryImpl(context = get()) }

    viewModel { InboxViewModel(get()) }
    viewModel { (room: StoreRoom) -> StoreRoomViewModel(
        getStoreItemsUseCase = get(),
        fetchStoreDataUseCase = get(),
        room = room,
    )}
}
