package com.caldeirasoft.outcast.ui.screen.store.topchartitem

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.screen.storedirectory.TopChartsViewModel
import com.caldeirasoft.outcast.ui.util.ScreenState
import com.caldeirasoft.outcast.ui.util.onError
import com.caldeirasoft.outcast.ui.util.onLoading
import com.caldeirasoft.outcast.ui.util.onSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.datetime.Clock
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun TopChartsPodcast() {
    val viewModel = getViewModel<TopChartsPodcastViewModel>()
    TopChartsContent(viewModel = viewModel)
}

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun TopChartsEpisode() {
    val viewModel = getViewModel<TopChartsEpisodeViewModel>()
    TopChartsContent(viewModel = viewModel)
}

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun TopChartsContent(
    viewModel: TopChartsItemViewModel
) {
    val viewState by viewModel.state.collectAsState()
    val actions = ActionsAmbient.current
    val topChartsItems = viewModel.topCharts.collectAsLazyPagingItems()

    TopChartsContent(
        state = viewState.screenState,
        actions = actions,
        topChartsItems = topChartsItems
    )
}

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun TopChartsContent(
    state: ScreenState,
    topChartsItems: LazyPagingItems<StoreItem>,
    actions: Actions
) {
    state
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
            StoreContentFeed(
                lazyPagingItems = topChartsItems,
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
}