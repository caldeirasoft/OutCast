package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.presentation.viewmodel.InboxViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


@ExperimentalCoroutinesApi
@FlowPreview
internal val appModule = module {
    viewModel { InboxViewModel(get()) }
}
