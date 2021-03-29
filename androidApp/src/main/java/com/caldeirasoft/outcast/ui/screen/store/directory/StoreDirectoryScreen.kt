@file:OptIn(KoinApiExtension::class)
package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.foundation.ViewPager
import com.caldeirasoft.outcast.ui.components.foundation.ViewPagerController
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg.Companion.toEpisodeArg
import com.caldeirasoft.outcast.ui.screen.store.topcharts.pagerTabIndicatorOffset
import com.caldeirasoft.outcast.ui.util.ifLoadingMore
import com.caldeirasoft.outcast.ui.util.px
import com.caldeirasoft.outcast.ui.util.toDp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import timber.log.Timber

enum class StoreGenreItem(val genreId: Int, @StringRes val titleId: Int, @DrawableRes val drawableId: Int) {
    Arts(1301, R.string.store_genre_1301, R.drawable.ic_color_palette),
    Business(1321, R.string.store_genre_1321, R.drawable.ic_analytics),
    Comedy(1303, R.string.store_genre_1303, R.drawable.ic_theater),
    Education(1304, R.string.store_genre_1304, R.drawable.ic_mortarboard),
    Fiction(1483, R.string.store_genre_1483, R.drawable.ic_fiction),
    Government(1511, R.string.store_genre_1511, R.drawable.ic_city_hall),
    Health_Fitness(1512, R.string.store_genre_1512, R.drawable.ic_first_aid_kit),
    History(1487, R.string.store_genre_1487, R.drawable.ic_history),
    Kids_Family(1305, R.string.store_genre_1305, R.drawable.ic_family),
    Leisure(1502, R.string.store_genre_1502, R.drawable.ic_game_controller),
    Music(1310, R.string.store_genre_1310, R.drawable.ic_guitar),
    News(1489, R.string.store_genre_1489, R.drawable.ic_news),
    Religion_Spirtuality(1314, R.string.store_genre_1314, R.drawable.ic_religion),
    Science(1533, R.string.store_genre_1533, R.drawable.ic_flasks),
    Society_Culture(1324, R.string.store_genre_1324, R.drawable.ic_social_care),
    Sports(1545, R.string.store_genre_1545, R.drawable.ic_sport),
    TV_Film(1309, R.string.store_genre_1309, R.drawable.ic_video_camera),
    Technology(1318, R.string.store_genre_1318, R.drawable.ic_artificial_intelligence),
    True_Crime(1488, R.string.store_genre_1488, R.drawable.ic_handcuffs)
}

@OptIn(ExperimentalPagerApi::class)
@ExperimentalAnimationApi
@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreDirectoryScreen(
    navigateTo: (Screen) -> Unit,
) {
    Timber.d("DBG - StoreDirectoryScreen recompose")
    val viewModel: StoreDirectoryViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()
    val lazyPagingItems = viewModel.discover.collectAsLazyPagingItems()

    val coroutineScope = rememberCoroutineScope()
    // Remember a PagerState with our tab count
    val pagerState = rememberPagerState(pageCount = 2)

    ReachableScaffold(headerRatio = 1 / 6f) { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        LazyListLayout(lazyListItems = lazyPagingItems)
        {
            val scrollState = rememberLazyListState()
            LazyColumn(
                state = scrollState,
                contentPadding = PaddingValues(top = spacerHeight.toDp()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp))
            {
                items(lazyPagingItems = lazyPagingItems) { collection ->
                    when (collection) {
                        is StoreCollectionFeatured ->
                            StoreCollectionFeaturedContent(
                                storeCollection = collection,
                                navigateTo = navigateTo,
                            )
                        is StoreCollectionPodcasts -> {
                            // content
                            StoreCollectionPodcastsContent(
                                storeCollection = collection,
                                navigateTo = navigateTo,
                                onHeaderLinkClick = { navigateTo(Screen.Room(collection.room)) },
                                followingStatus = state.followingStatus,
                                onSubscribeClick = viewModel::subscribeToPodcast,
                            )
                        }
                        is StoreCollectionEpisodes -> {
                            // content
                            StoreCollectionEpisodesContent(
                                storeCollection = collection,
                                numRows = 3,
                                navigateTo = navigateTo,
                                onHeaderLinkClick = { navigateTo(Screen.Room(collection.room)) }
                            )
                        }
                        is StoreCollectionCharts -> {
                            // content
                            StoreCollectionChartsContent(
                                storeCollection = collection,
                                state = state,
                                navigateTo = navigateTo,
                                onSubscribeClick = viewModel::subscribeToPodcast,
                                coroutineScope = coroutineScope,
                                pagerState = pagerState
                            )
                        }
                        is StoreCollectionRooms -> {
                            // genres
                            StoreCollectionRoomsContent(
                                storeCollection = collection,
                                navigateTo = navigateTo
                            )
                        }
                    }
                }

                lazyPagingItems.ifLoadingMore {
                    item {
                        Text(
                            modifier = Modifier.padding(
                                vertical = 16.dp,
                                horizontal = 4.dp
                            ),
                            text = "Loading next"
                        )
                    }
                }
            }

            ReachableAppBar(
                title = {
                    Text(text = stringResource(id = R.string.store_tab_discover))
                },
                actions = { },
                state = scrollState,
                headerHeight = headerHeight)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun StoreCollectionChartsContent(
    storeCollection: StoreCollectionCharts,
    state: StoreDirectoryViewState,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
    navigateTo: (Screen) -> Unit,
    onSubscribeClick: (StorePodcast) -> Unit = { },
) {
    var fullHeight by remember { mutableStateOf(500.dp) }
    var heights by remember { mutableStateOf(mapOf<Int, Int>()) }

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.Transparent,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        )
        {
            StoreItemType.values().forEachIndexed { index, tab ->
                Tab(
                    selected = (index == pagerState.currentPage),
                    onClick = {
                        // Animate to the selected page when clicked
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(tab.ordinal)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(id = when (tab) {
                                StoreItemType.PODCAST -> R.string.store_podcasts
                                StoreItemType.EPISODE -> R.string.store_episodes
                            }),
                            style = MaterialTheme.typography.body2)
                    }
                )
            }
        }

        fullHeight = heights[pagerState.currentPage]?.toDp() ?: 600.dp
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.height(fullHeight)
        ) { page ->
            val topCharts: List<StoreItem> = when (page) {
                0 -> storeCollection.topPodcasts
                else -> storeCollection.topEpisodes
            }
            Column(modifier = Modifier
                .wrapContentHeight()
                .onSizeChanged {
                    if (!heights.containsKey(page))
                        heights = heights.plus(page to it.height)
                }) {
                topCharts.forEachIndexed { index, storeItem ->
                    when (storeItem) {
                        is StorePodcast -> {
                            PodcastListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = {
                                        navigateTo(Screen.StorePodcastScreen(storeItem))
                                    }),
                                storePodcast = storeItem,
                                index = index + 1,
                                followingStatus = state.followingStatus[storeItem.id],
                                onSubscribeClick = onSubscribeClick
                            )
                            Divider()
                        }
                        is StoreEpisode -> {
                            StoreEpisodeItem(
                                episode = storeItem.episode,
                                modifier = Modifier.fillMaxWidth(),
                                onPodcastClick = {
                                    navigateTo(Screen.StorePodcastScreen(storeItem.podcast))
                                },
                                onEpisodeClick = { navigateTo(Screen.EpisodeScreen(storeItem.toEpisodeArg())) },
                                index = index + 1
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopChartsTabRow(
    selectedChartTab: StoreItemType,
    onChartSelected: (StoreItemType) -> Unit,
    pagerController: ViewPagerController,
) {
    TabRow(
        selectedTabIndex = selectedChartTab.ordinal,
        backgroundColor = Color.Transparent
    )
    {
        StoreItemType.values().forEachIndexed { index, tab ->
            Tab(
                selected = (index == selectedChartTab.ordinal),
                onClick = {
                    onChartSelected(tab)
                    pagerController.moveTo(tab.ordinal)
                },
                text = {
                    Text(
                        text = stringResource(id = when (tab) {
                            StoreItemType.PODCAST -> R.string.store_podcasts
                            StoreItemType.EPISODE -> R.string.store_episodes
                        }),
                        style = MaterialTheme.typography.body2)
                }
            )
        }
    }
}


@Composable
private fun TopChartsTabContent(
    storeCollection: StoreCollectionCharts,
    state: StoreDirectoryViewState,
    selectedChartTab: StoreItemType,
    onChartSelected: (StoreItemType) -> Unit,
    navigateTo: (Screen) -> Unit,
    onSubscribeClick: (StorePodcast) -> Unit = { },
) {
    ViewPager(
        modifier = Modifier.fillMaxWidth(),
        range = 0..1,
        initialPage = selectedChartTab.ordinal,
        onPageChanged = { onChartSelected(StoreItemType.values()[it]) }
    ) {
        val page = this.index
        val topCharts: List<StoreItem> = when (page) {
            0 -> storeCollection.topPodcasts
            else -> storeCollection.topEpisodes
        }
        Column {
            topCharts.forEachIndexed { index, storeItem ->
                when (storeItem) {
                    is StorePodcast -> {
                        PodcastListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    navigateTo(Screen.StorePodcastScreen(storeItem))
                                }),
                            storePodcast = storeItem,
                            index = index + 1,
                            followingStatus = state.followingStatus[storeItem.id],
                            onSubscribeClick = onSubscribeClick
                        )
                        Divider()
                    }
                    is StoreEpisode -> {
                        StoreEpisodeItem(
                            episode = storeItem.episode,
                            modifier = Modifier.fillMaxWidth(),
                            onPodcastClick = { navigateTo(Screen.StorePodcastScreen(storeItem.podcast)) },
                            onEpisodeClick = { navigateTo(Screen.EpisodeScreen(storeItem.toEpisodeArg())) },
                            index = index + 1
                        )
                        Divider()
                    }
                }
            }
        }
    }
}
