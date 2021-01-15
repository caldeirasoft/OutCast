package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.emptyContent
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.itemsIndexed
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.util.viewModelProviderFactoryOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow


@ExperimentalCoroutinesApi
@Composable
fun TopChartsScreen(
    genreId: Int,
    storeItemType: StoreItemType,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit,
) {
    val viewModel: TopChartsViewModel = viewModel(
        key = "store_chart_${genreId}",
        factory = viewModelProviderFactoryOf { TopChartsViewModel(genreId) }
    )
    val viewState by viewModel.state.collectAsState()
    TopChartsScreen(
        viewState = viewState,
        topPodcastsCharts = viewModel.topPodcastsCharts,
        topEpisodesCharts = viewModel.topEpisodesCharts,
        onChartTabSelected = viewModel::onTabSelected,
        navigateToPodcast = navigateToPodcast,
        navigateUp = navigateUp
    )
}

@ExperimentalCoroutinesApi
@Composable
fun TopChartsScreen(
    viewState: TopChartsViewModel.State,
    topPodcastsCharts: Flow<PagingData<StoreItem>>,
    topEpisodesCharts: Flow<PagingData<StoreItem>>,
    onChartTabSelected: (StoreItemType) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "title")
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                },
                actions = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Filled.Search)
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    )
    {
        Column {
            TabRow(
                selectedTabIndex = viewState.selectedChartTab.ordinal,
                backgroundColor = Color.Transparent,
                divider = emptyContent(),
                indicator = emptyTabIndicator
            )
            {
                StoreItemType.values().forEachIndexed { index, tab ->
                    ChoiceChipTab(
                        selected = (index == viewState.selectedChartTab.ordinal),
                        onClick = { onChartTabSelected(tab) },
                        text = stringResource(id = when (tab) {
                            StoreItemType.PODCAST -> R.string.store_tab_chart_podcasts
                            StoreItemType.EPISODE -> R.string.store_tab_chart_episodes
                        })
                    )
                }
            }
            when (viewState.selectedChartTab) {
                StoreItemType.PODCAST ->
                    TopChartTabContent(
                        topPodcastsCharts,
                        navigateToPodcast = navigateToPodcast
                    )
                StoreItemType.EPISODE ->
                    TopChartTabContent(
                        topEpisodesCharts,
                        navigateToPodcast = navigateToPodcast
                    )
            }
        }
    }
}

private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}

@ExperimentalCoroutinesApi
@Composable
private fun TopChartTabContent(
    topCharts: Flow<PagingData<StoreItem>>,
    navigateToPodcast: (String) -> Unit,
) {
    DiscoverContent(
        discover = topCharts,
        loadingContent = { ShimmerStorePodcastList() },
    ) { lazyPagingItems ->
        itemsIndexed(lazyPagingItems = lazyPagingItems) { index, item ->
            when (item) {
                is StorePodcast -> {
                    PodcastListItemIndexed(
                        modifier = Modifier.fillMaxWidth()
                            .clickable(onClick = { navigateToPodcast(item.url) }),
                        storePodcast = item,
                        index = index + 1
                    )
                    Divider()
                }
                is StoreEpisode -> {
                    StoreEpisodeItemFromCharts(
                        onEpisodeClick = { navigateToPodcast(item.podcastEpisodeWebsiteUrl.orEmpty()) },
                        onThumbnailClick = { navigateToPodcast(item.podcastEpisodeWebsiteUrl.orEmpty()) },
                        storeEpisode = item,
                        index = index + 1
                    )
                    Divider()
                }
            }
        }
    }
}
