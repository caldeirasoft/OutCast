package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreChart
import com.caldeirasoft.outcast.domain.models.store.StoreDirectory
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf


@ExperimentalCoroutinesApi
@Composable
fun TopChartsContent(
    topChart: StoreChart,
    storeDirectory: StoreDirectory,
) {
    /*//val viewModel = getViewModel<TopChartsViewModel> { parametersOf(topChart, storeDirectory) }
    val viewModel: TopChartsViewModel = viewModel(
        key = "store_chart_${topChart.id}",
        factory = viewModelProviderFactoryOf { TopChartsViewModel(topChart, storeDirectory) }
    )
    val topCharts = viewModel.topCharts
    val actions = ActionsAmbient.current
    TopChartsContent(topCharts = topCharts, actions = actions)*/
}

@ExperimentalCoroutinesApi
@Composable
fun TopChartsContent(
    topCharts: Flow<PagingData<StoreItem>>,
    actions: Actions
) {
    val pagedList = topCharts.collectAsLazyPagingItems()
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