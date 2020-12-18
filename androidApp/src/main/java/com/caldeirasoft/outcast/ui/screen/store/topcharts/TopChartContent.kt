package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.StoreChart
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StoreTopCharts
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow


@ExperimentalCoroutinesApi
@Composable
fun TopChartsScreen(
    topCharts: StoreTopCharts
) {
    val viewModel: TopChartsViewModel = viewModel(
        key = "store_chart_${topCharts.id}",
        factory = viewModelProviderFactoryOf { TopChartsViewModel(topCharts) }
    )
    val topPodcastsCharts = viewModel.topPodcastsCharts
    val topEpisodesCharts = viewModel.topEpisodesCharts
    val actions = ActionsAmbient.current
    TopChartsScreen(
        topPodcastsCharts = topPodcastsCharts,
        topEpisodesCharts = topEpisodesCharts,
        actions = actions)
}

@ExperimentalCoroutinesApi
@Composable
fun TopChartsScreen(
    topPodcastsCharts: Flow<PagingData<StoreItem>>,
    topEpisodesCharts: Flow<PagingData<StoreItem>>,
    actions: Actions
) {
    val pagedList = topPodcastsCharts.collectAsLazyPagingItems()
    StoreContentFeed(
        lazyPagingItems = pagedList,
        actions = actions
    ) { item, index ->
        when (item) {
            is StorePodcast -> {
                StorePodcastListItemIndexed(podcast = item, index = index + 1)
                Divider()
            }
            is StoreEpisode -> {
                StoreEpisodeListItem(episode = item/*, index = index + 1*/)
                Divider()
            }
        }
    }
}