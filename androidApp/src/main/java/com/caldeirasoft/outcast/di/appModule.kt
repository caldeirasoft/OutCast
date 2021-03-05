package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeViewModel
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastViewModel
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


@ExperimentalCoroutinesApi
@FlowPreview
internal val appModule = module {
    //viewModel { InboxViewModel(get()) }

    viewModel { (storeItemType: StoreItemType) ->
        TopChartsViewModel(
            storeItemType = storeItemType,
            fetchStoreFrontUseCase = get(),
            getStoreItemsUseCase = get(),
            fetchStoreTopChartsIdsUseCase = get()
        )
    }
    viewModel { (storePodcast: StorePodcast) ->
        StorePodcastViewModel(
            storePodcast = storePodcast,
            fetchStorePodcastDataUseCase = get(),
            fetchStoreFrontUseCase = get(),
            getStoreItemsUseCase = get()
        )
    }
    viewModel { (storeEpisode: StoreEpisode) ->
        EpisodeViewModel(
            storeEpisode = storeEpisode,
            fetchStoreFrontUseCase = get(),
            fetchStoreEpisodeDataUseCase = get()
        )
    }
}
