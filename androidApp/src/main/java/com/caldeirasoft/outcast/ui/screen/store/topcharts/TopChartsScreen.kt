package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.ChoiceChipTab
import com.caldeirasoft.outcast.ui.components.EpisodeListItemIndexed
import com.caldeirasoft.outcast.ui.components.PodcastListItemIndexed
import com.caldeirasoft.outcast.ui.components.StoreContentFeed
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
        TopChartsContent(
            genreId = genreId,
            storeItemType = storeItemType,
            navigateToPodcast = navigateToPodcast)
    }
}

@Composable
fun TopChartsContent(
    genreId: Int? = null,
    storeItemType: StoreItemType = StoreItemType.PODCAST,
    navigateToPodcast: (String) -> Unit,
) {
    val viewModel: TopChartsViewModel = viewModel(
        key = "store_chart_${genreId}",
        factory = viewModelProviderFactoryOf { TopChartsViewModel(genreId) }
    )
    TopChartsContent(
        viewModel = viewModel,
        storeItemType = storeItemType,
        navigateToPodcast = navigateToPodcast
    )
}

private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}

@Composable
private fun TopChartsContent(
    viewModel: TopChartsViewModel,
    storeItemType: StoreItemType,
    navigateToPodcast: (String) -> Unit,
) {
    val topPodcastsCharts = viewModel.topPodcastsCharts
    val topEpisodesCharts = viewModel.topEpisodesCharts
    var selectedChartTab: StoreItemType by remember { mutableStateOf(storeItemType) }
    val onChartSelected: (StoreItemType) -> Unit = {
        selectedChartTab = it
    }
    Column {
        TabRow(
            selectedTabIndex = selectedChartTab.ordinal,
            backgroundColor = Color.Transparent,
            divider = emptyContent(),
            indicator = emptyTabIndicator
        )
        {
            StoreItemType.values().forEachIndexed { index, tab ->
                ChoiceChipTab(
                    selected = (index == selectedChartTab.ordinal),
                    onClick = { onChartSelected(tab) },
                    text = stringResource(id = when(tab) {
                        StoreItemType.PODCAST -> R.string.store_tab_chart_podcasts
                        StoreItemType.EPISODE -> R.string.store_tab_chart_episodes
                    })
                )
            }
        }
        when (selectedChartTab) {
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

@ExperimentalCoroutinesApi
@Composable
private fun TopChartTabContent(
    topCharts: Flow<PagingData<StoreItem>>,
    navigateToPodcast: (String) -> Unit,
) {
    val pagedList = topCharts.collectAsLazyPagingItems()
    StoreContentFeed(
        lazyPagingItems = pagedList
    ) { item, index ->
        when (item) {
            is StorePodcast -> {
                PodcastListItemIndexed(
                    modifier = Modifier.fillMaxWidth()
                        .clickable(onClick = { navigateToPodcast(item.url) }),
                    storePodcast = item,
                    index = index + 1)
                Divider()
            }
            is StoreEpisode -> {
                EpisodeListItemIndexed(
                    modifier = Modifier.fillMaxWidth()
                        .clickable(onClick = { }),
                    storeEpisode = item,
                    index = index + 1)
                Divider()
            }
        }
    }
}
