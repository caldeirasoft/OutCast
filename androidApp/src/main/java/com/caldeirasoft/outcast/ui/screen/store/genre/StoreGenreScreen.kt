package com.caldeirasoft.outcast.ui.screen.store.genre

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.util.px
import com.caldeirasoft.outcast.ui.util.viewModelProviderFactoryOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreGenreScreen(
    genreId: Int,
    title: String,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateToTopCharts: (Int, StoreItemType) -> Unit,
    navigateUp: () -> Unit
) {
    val viewModel: StoreGenreViewModel = viewModel(
        key = genreId.toString(),
        factory = viewModelProviderFactoryOf { StoreGenreViewModel(genreId) }
    )
    val viewState by viewModel.state.collectAsState()

    Log.d("Compose", "Compose StoreGenreScreen : ${Clock.System.now()}")

    StoreGenreContent(
        title = title,
        viewState = viewState,
        discover = viewModel.discover,
        onChartTabSelected = viewModel::onChartTabSelected,
        navigateToRoom = navigateToRoom,
        navigateToPodcast = navigateToPodcast,
        navigateToTopCharts = navigateToTopCharts,
        navigateUp = navigateUp,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StoreGenreContent(
    title: String,
    viewState: StoreGenreViewModel.State,
    discover: Flow<PagingData<StoreItem>>,
    onChartTabSelected: (StoreItemType) -> Unit,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToTopCharts: (Int, StoreItemType) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit,
) {
    val listState = rememberLazyListState(0)
    val lazyPagingItems = discover.collectAsLazyPagingItems()

    ReachableScaffold { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)) {
            item {
                with(AmbientDensity.current) {
                    Spacer(modifier = Modifier.height(spacerHeight.toDp()))
                }
            }

            DiscoverContents(
                lazyPagingItems = lazyPagingItems,
                loadingContent = { ShimmerStoreCollectionsList() },
            ) { lazyPagingItems ->
                items(lazyPagingItems = lazyPagingItems) { item ->
                    when (item) {
                        is StoreCollectionPodcasts ->
                            StoreCollectionPodcastsContent(
                                storeCollection = item,
                                navigateToRoom = navigateToRoom,
                                navigateToPodcast = navigateToPodcast,
                            )
                        is StoreCollectionEpisodes ->
                            StoreCollectionEpisodesContent(
                                storeCollection = item
                            )
                        is StoreCollectionCharts ->
                            StoreCollectionChartsContent(
                                storeCollection = item,
                                selectedTab = viewState.selectedChartTab,
                                onTabSelected = onChartTabSelected,
                                navigateToTopCharts = navigateToTopCharts,
                                navigateToPodcast = navigateToPodcast,
                            )
                        is StoreCollectionRooms ->
                            StoreCollectionRoomsContent(
                                storeCollection = item,
                                navigateToRoom = navigateToRoom
                            )
                        is StoreCollectionFeatured ->
                            StoreCollectionFeaturedContent(
                                storeCollection = item
                            )
                    }
                }
            }
        }

        ReachableHeader(
            title = { Text(text = title) },
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(Icons.Filled.ArrowBack)
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Filled.Search)
                }
            },
            state = listState,
            headerHeight = headerHeight)
    }
}

@Composable
fun StoreCollectionChartsContent(
    storeCollection: StoreCollectionCharts,
    selectedTab: StoreItemType,
    onTabSelected: (StoreItemType) -> Unit,
    navigateToTopCharts: (Int, StoreItemType) -> Unit,
    navigateToPodcast: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(
            vertical = 16.dp
        )
    ) {
        StoreHeadingSectionWithLink(
            title = stringResource(R.string.store_charts),
            onClick = { navigateToTopCharts(storeCollection.genreId, selectedTab) }
        )
        Spacer(modifier = Modifier.preferredHeight(8.dp))
        TopChartsTabContent(
            storeCollection = storeCollection,
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            navigateToPodcast = navigateToPodcast,
        )
    }
}

private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}


@Composable
private fun TopChartsTabContent(
    storeCollection: StoreCollectionCharts,
    selectedTab: StoreItemType,
    onTabSelected: (StoreItemType) -> Unit,
    navigateToPodcast: (String) -> Unit,
) {
    Column {
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            backgroundColor = Color.Transparent,
            divider = emptyContent(),
            indicator = emptyTabIndicator
        )
        {
            StoreItemType.values().forEachIndexed { index, tab ->
                ChoiceChipTab(
                    selected = (index == selectedTab.ordinal),
                    onClick = { onTabSelected(tab) },
                    text = stringResource(id = when(tab) {
                        StoreItemType.PODCAST -> R.string.store_tab_chart_podcasts
                        StoreItemType.EPISODE -> R.string.store_tab_chart_episodes
                    })
                )
            }
        }
        when (selectedTab) {
            StoreItemType.PODCAST ->
                TopChartContent(
                    storeCollection.topPodcasts,
                    navigateToPodcast = navigateToPodcast,
                )
            StoreItemType.EPISODE ->
                TopChartContent(
                    storeCollection.topEpisodes,
                    navigateToPodcast = navigateToPodcast,
                )
        }
    }
}

@Composable
private fun ColumnScope.TopChartContent(
    topCharts: List<StoreItem>,
    navigateToPodcast: (String) -> Unit,
) {
    topCharts.forEachIndexed { index, storeItem ->
        when (storeItem) {
            is StorePodcast -> {
                SmallPodcastListItemIndexed(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { navigateToPodcast(storeItem.url) }),
                    storePodcast = storeItem,
                    index = index + 1)
                Divider()
            }
            is StoreEpisode -> {
                StoreEpisodeSmallListItemIndexed(
                    episode = storeItem,
                    index = index + 1,
                    navigateToEpisode = {})
                Divider()
            }
        }
    }
}

