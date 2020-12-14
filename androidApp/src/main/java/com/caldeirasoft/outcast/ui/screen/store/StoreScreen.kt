package com.caldeirasoft.outcast.ui.screen.store

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.material.icons.twotone.ConnectWithoutContact
import androidx.compose.material.icons.twotone.Masks
import androidx.compose.material.icons.twotone.NewReleases
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartsContent
import com.caldeirasoft.outcast.ui.util.ScreenState
import com.caldeirasoft.outcast.ui.util.onError
import com.caldeirasoft.outcast.ui.util.onLoading
import com.caldeirasoft.outcast.ui.util.onSuccess
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import org.koin.androidx.compose.getViewModel

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreScreen(viewModel: StoreViewModel = getViewModel()) {
    val actions = ActionsAmbient.current
    val viewState by viewModel.state.collectAsState()
    val genres by viewModel.genres.collectAsState()

    Log.d("Compose", "Compose StoreDirectoryScreen : ${Clock.System.now()}")

    StoreTabLayout(
        state = viewState.screenState,
        tabs = viewState.storeTabs,
        selectedTab = viewState.selectedStoreTab,
        onTabSelected = viewModel::onStoreTabSelected,
        charts = viewState.chartList,
        selectedChart = viewState.selectedChart,
        onChartSelected = viewModel::onChartSelected,
        discover = viewModel.discover,
        topPodcastsChart = viewModel.topPodcastsChart,
        topEpisodesChart = viewModel.topEpisodesChart,
        genres = genres,
        actions = actions
    )
}

@Composable
fun StoreTabLayout(
    state: ScreenState,
    tabs: List<StoreTab>,
    selectedTab: StoreTab,
    onTabSelected: (StoreTab) -> Unit,
    charts: List<Chart>,
    selectedChart: Chart,
    onChartSelected: (Chart) -> Unit,
    discover: Flow<PagingData<StoreItem>>,
    topPodcastsChart: Flow<PagingData<StoreItem>>,
    topEpisodesChart: Flow<PagingData<StoreItem>>,
    genres: List<StoreGenre>,
    actions: Actions
) {
    Log.d("Compose", "Compose StoreTabLayout : ${Clock.System.now()}")
    val selectedTabIndex = tabs.indexOfFirst { it == selectedTab }

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
                        Icon(imageVector = Icons.Filled.HourglassFull)
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
                                    StoreTab.TOP_CHARTS -> "Top charts"
                                    StoreTab.GENRES -> "Genres"
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
                        actions = actions
                    )
                    StoreTab.TOP_CHARTS.ordinal -> TopChartsTabContent(
                        state = state,
                        charts = charts,
                        selectedChart = selectedChart,
                        onChartSelected = onChartSelected,
                        topPodcastsChart = topPodcastsChart,
                        topEpisodesChart = topEpisodesChart,
                        actions = actions
                    )
                    StoreTab.GENRES.ordinal -> GenreListContent(
                        state = state,
                        genres = genres,
                        actions = actions
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
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
            Column {
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
}

@ExperimentalCoroutinesApi
@Composable
fun TopChartsTabContent (
    state: ScreenState,
    charts: List<Chart>,
    selectedChart: Chart,
    onChartSelected: (Chart) -> Unit,
    topPodcastsChart: Flow<PagingData<StoreItem>>,
    topEpisodesChart: Flow<PagingData<StoreItem>>,
    actions: Actions
) {
    val selectedIndex = charts.indexOfFirst { it == selectedChart }
    state
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
            Column {
                TabRow(
                    selectedTabIndex = selectedIndex,
                    backgroundColor = Color.Transparent
                )
                {
                    charts.forEachIndexed { index, tab ->
                        Tab(
                            selected = (index == selectedIndex),
                            onClick = { onChartSelected(tab) },
                            text = {
                                Text(
                                    text = when (tab) {
                                        Chart.PODCASTS -> "Podcasts"
                                        Chart.EPISODES -> "Episodes"
                                    },
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        )
                    }
                }
                Surface(modifier = Modifier.weight(0.5f)) {
                    val pagedList = when (charts[selectedIndex]) {
                        Chart.PODCASTS -> topPodcastsChart
                        Chart.EPISODES -> topEpisodesChart
                    }
                    TopChartContent(topChart = pagedList, actions = actions)
                }
            }
        }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TopChartContent(
    topChart: Flow<PagingData<StoreItem>>,
    actions: Actions) {
    val pagedList = topChart.collectAsLazyPagingItems()
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
                StoreEpisodeListItem(episode = item)
                Divider()
            }
        }
    }
}

@Composable
fun GenreListContent(
    state: ScreenState,
    genres: List<StoreGenre>,
    actions: Actions
) {
    state
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
            LazyColumnFor(items = genres) { genre ->
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            actions.navigateToStoreCollection(
                                StoreRoom(
                                    id = 0,
                                    label = genre.name,
                                    url = genre.url,
                                    storeFront = genre.storeFront
                                )
                            )
                        }),
                    text = { Text(text = genre.name) },
                    icon = {
                        //https://github.com/husaynhakeem/android-playground/blob/804fc7ed1d63e4e1d2c9f3cad86194e38769dbe4/ComposeStateSample/app/src/main/java/com/husaynhakeem/composestatesample/widget/PokemonSprite.kt
                        CoilImage(
                            imageModel = genre.artwork.url,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .preferredSize(56.dp),
                        )
                        /*
                            when(genre.id) {
                                1301 -> Icons.TwoTone. //arts
                                1321 -> Icons.TwoTone. //business
                                1303 -> Icons.TwoTone. //comedy
                                1304 -> Icons.TwoTone. //education
                                1483 -> Icons.TwoTone. //fiction
                                1511 -> Icons.TwoTone. //government
                                1512 -> Icons.TwoTone. //health & fitness
                                1487 -> Icons.TwoTone. //history
                                1305 -> Icons.TwoTone. //kids & family
                                1502 -> Icons.TwoTone. //leisure
                                1310 -> Icons.TwoTone. //music
                                1489 -> Icons.TwoTone. //news
                                1314 -> Icons.TwoTone. //religion & spirituality
                                1533 -> Icons.TwoTone. //sciences
                                1324 -> Icons.TwoTone. //culture & society
                                1545 -> Icons.TwoTone. //sports
                                1309 -> Icons.TwoTone. //tv & film
                                1318 -> Icons.TwoTone. //technology
                                1488 -> Icons.TwoTone. //true crime
                                else -> Icons.Filled.Article
                            }
                             */
                    }
                )
                Divider()
            }
        }
}

private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun GenreTabs(
    genreMap: StoreGenreMapData,
    selectedGenres: List<StoreGenre>,
    onGenreSelected: (StoreGenre, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    ScrollableRow(
        modifier = modifier,
        scrollState = scrollState
    ) {
        Surface(
            //color = MaterialTheme.colors.onSurface.copy(alpha = 0.48f),
            contentColor = MaterialTheme.colors.onSurface,
            border = BorderStroke(width = 1.dp,
                if (selectedGenres.drop(1).isNotEmpty())
                    MaterialTheme.colors.primary
                else MaterialTheme.colors.onSurface.copy(alpha = 0.48f)
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row {
                selectedGenres.forEachIndexed { i, storeGenre ->
                    genreMap.genreChildren[storeGenre.id]?.forEachIndexed { index, category ->
                        AnimatedVisibility(
                            visible = selectedGenres.size - 1 <= i || selectedGenres.contains(category),
                            enter = expandHorizontally(animSpec = tween(durationMillis = 250))
                                    + fadeIn(animSpec = tween(durationMillis = 250)),
                            exit = shrinkHorizontally(animSpec = tween(durationMillis = 250))
                                    + fadeOut(animSpec = tween(durationMillis = 250)),
                        ) {
                            ChoiceChipContent(
                                text = category.name,
                                selected = selectedGenres.contains(category),
                                selectedGenres = selectedGenres,
                                onGenreClick = {
                                    onGenreSelected(category, i + 1)
                                    scrollState.smoothScrollTo(0f)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChoiceChipContent(
    text: String,
    selected: Boolean,
    selectedGenres: List<StoreGenre>,
    modifier: Modifier = Modifier,
    onGenreClick: () -> Unit,
) {
    val backgroundColor: Color = when {
        selected -> MaterialTheme.colors.primary
        else -> Color.Transparent
    }
    TextButton(
        colors = ButtonConstants.defaultTextButtonColors(
            backgroundColor = animate(backgroundColor),
            contentColor = animate(contentColorFor(backgroundColor)),
            disabledContentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.disabled)
        ),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(0.dp),
        onClick = onGenreClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
