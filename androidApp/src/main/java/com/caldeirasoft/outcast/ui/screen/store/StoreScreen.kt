package com.caldeirasoft.outcast.ui.screen.store

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.util.ScreenState
import com.caldeirasoft.outcast.ui.util.onError
import com.caldeirasoft.outcast.ui.util.onLoading
import com.caldeirasoft.outcast.ui.util.onSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import org.koin.androidx.compose.getViewModel

typealias NavigateToStoreEntryCallBack = (String) -> Unit



@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreScreen(viewModel: StoreViewModel = getViewModel()) {
    val actions = ActionsAmbient.current
    val viewState by viewModel.state.collectAsState()

    println("Compose StoreDirectoryScreen : ${Clock.System.now()}")

    StoreTabLayout(
        state = viewState.screenState,
        tabs = viewState.storeTabs,
        selectedTab = viewState.selectedStoreTab,
        onTabSelected = viewModel::onStoreTabSelected,
        topChartsTabs = viewState.topChartsTab,
        selectedTopChartsTab = viewState.selectedTopChartsTab,
        onTopChartsTabSelected = viewModel::onTopChartsTabSelected,
        discover = viewModel.discover,
        topChartsPodcasts = viewModel.topChartsPodcasts,
        topChartsEpisodes = viewModel.topChartsEpisodes,
        actions = actions
    )
}

@Composable
fun StoreTabLayout(
    state: ScreenState,
    tabs: List<StoreTab>,
    selectedTab: StoreTab,
    onTabSelected: (StoreTab) -> Unit,
    topChartsTabs: List<TopChartsTab>,
    selectedTopChartsTab: TopChartsTab,
    onTopChartsTabSelected: (TopChartsTab) -> Unit,
    discover: Flow<PagingData<StoreItem>>,
    topChartsPodcasts: Flow<PagingData<StoreItem>>,
    topChartsEpisodes: Flow<PagingData<StoreItem>>,
    actions: Actions
) {
    val selectedTabIndex = tabs.indexOfFirst { it == selectedTab }
    val selectedTopChartsTabIndex = topChartsTabs.indexOfFirst { it == selectedTopChartsTab }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Discover")
                },
                navigationIcon = {
                    IconButton(onClick = actions.navigateUp) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                },
                actions = {
                    IconButton(onClick = actions.navigateUp) {
                        Icon(asset = Icons.Filled.HourglassFull)
                    }
                },
                backgroundColor = Color.Transparent
            )
        }
    )
    {
        Column {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = Color.Transparent
            )
            {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = (index == selectedTabIndex),
                        onClick = { onTabSelected(tab) },
                        text = {
                            Text(
                                text = when (tab) {
                                    StoreTab.DISCOVER -> "Discover"
                                    StoreTab.CATEGORIES -> "Categories"
                                    StoreTab.CHARTS -> "Charts"
                                },
                                style = MaterialTheme.typography.body2
                            )
                        }
                    )
                }
            }
            Surface(modifier = Modifier.weight(0.5f)) {
                when (selectedTabIndex) {
                    StoreTab.DISCOVER.ordinal -> DiscoverContent(
                        state = state,
                        discover = discover,
                        actions = actions)
                    StoreTab.CHARTS.ordinal ->
                        Column {
                            TabRow(
                                selectedTabIndex = selectedTopChartsTabIndex,
                                backgroundColor = Color.Transparent
                            )
                            {
                                topChartsTabs.forEachIndexed { index, tab ->
                                    Tab(
                                        selected = (index == selectedTopChartsTabIndex),
                                        onClick = { onTopChartsTabSelected(tab) },
                                        text = {
                                            Text(
                                                text = when (tab) {
                                                    TopChartsTab.PODCASTS -> "Podcasts"
                                                    TopChartsTab.EPISODES -> "Episodes"
                                                },
                                                style = MaterialTheme.typography.body2
                                            )
                                        }
                                    )
                                }
                            }
                            Surface(modifier = Modifier.weight(0.5f)) {
                                when (selectedTopChartsTabIndex) {
                                    TopChartsTab.PODCASTS.ordinal -> TopChartsContent(
                                        state = state,
                                        topCharts = topChartsPodcasts,
                                        actions = actions
                                    )
                                    TopChartsTab.EPISODES.ordinal -> TopChartsContent(
                                        state = state,
                                        topCharts = topChartsEpisodes,
                                        actions = actions
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}

@Composable
fun DiscoverContent(
    state: ScreenState,
    discover: Flow<PagingData<StoreItem>>,
    actions: Actions)
{
    state
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
            val pagedList = discover.collectAsLazyPagingItems()
            StoreContentFeed(
                lazyPagingItems = pagedList,
                actions = actions
            ) { item, index ->
                when (item) {
                    is StorePodcast -> {
                        StorePodcastListItem(podcast = item)
                        Divider()
                    }
                    is StoreCollectionPodcasts ->
                        StoreCollectionPodcastsContent(storeCollection = item)
                    is StoreCollectionEpisodes ->
                        StoreCollectionEpisodesContent(storeCollection = item)
                    is StoreCollectionRooms ->
                        StoreCollectionRoomsContent(storeCollection = item)
                    is StoreCollectionFeatured ->
                        StoreCollectionFeaturedContent(storeCollection = item)
                }
            }
        }
}

@ExperimentalCoroutinesApi
@Composable
fun TopChartsContent(
    state: ScreenState,
    topCharts: Flow<PagingData<StoreItem>>,
    actions: Actions
) {
    state
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
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
}