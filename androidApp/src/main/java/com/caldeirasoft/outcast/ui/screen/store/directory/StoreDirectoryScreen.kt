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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreDirectory
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import com.caldeirasoft.outcast.domain.models.store.StoreGenreMapData
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.screen.store.base.StoreRoomBaseViewModel
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreDirectoryViewModel
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartContent
import com.caldeirasoft.outcast.ui.theme.FetchDominantColorFromPoster
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.util.ScreenState
import com.caldeirasoft.outcast.ui.util.onError
import com.caldeirasoft.outcast.ui.util.onLoading
import com.caldeirasoft.outcast.ui.util.onSuccess
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

typealias StoreDirectoryState = StoreRoomBaseViewModel.State<StoreDirectory>

private enum class StoreDirectoryTab(
    val titleId: Int,
) {
    Discover(R.string.store_tab_discover),
    Charts(R.string.store_tab_charts),
    Categories(R.string.store_tab_categories),
}

private val AmbientDominantColor = ambientOf<MutableState<Color>> { error("No dominant color") }


@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreDirectoryScreen() {
    val actions = ActionsAmbient.current
    val viewModel: StoreDirectoryViewModel = viewModel()
    val viewState by viewModel.state.collectAsState()
    var selectedTab: StoreDirectoryTab by remember { mutableStateOf(StoreDirectoryTab.Discover) }
    var selectedChartTab: StoreChartTab by remember { mutableStateOf(StoreChartTab.Podcasts) }
    val genres by viewModel.genres.collectAsState()

    Log.d("Compose", "Compose StoreDirectoryScreen : ${Clock.System.now()}")

    StoreDirectoryContent(
        state = viewState,
        discover = viewModel.storeDataPagedList,
        genres = genres,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        selectedChartTab = selectedChartTab,
        onChartSelected = { selectedChartTab = it },
        actions = actions
    )
}

@Composable
private fun StoreDirectoryContent(
    state: StoreDirectoryState,
    discover: Flow<PagingData<StoreItem>>,
    genres: List<StoreGenre>,
    selectedTab: StoreDirectoryTab,
    onTabSelected: (StoreDirectoryTab) -> Unit,
    selectedChartTab: StoreChartTab,
    onChartSelected: (StoreChartTab) -> Unit,
    actions: Actions
) {
    Log.d("Compose", "Compose StoreTabLayout : ${Clock.System.now()}")
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
            Tabs(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected)

            Surface(modifier = Modifier.weight(0.5f)) {
                when (selectedTab) {
                    StoreDirectoryTab.Discover -> DiscoverContent(
                        state = state.screenState,
                        discover = discover,
                        actions = actions
                    )
                    StoreDirectoryTab.Charts -> TopChartsTabContent(
                        state = state.screenState,
                        selectedChartTab = selectedChartTab,
                        onChartSelected = onChartSelected,
                        storeDirectory = state.storeData
                    )
                    StoreDirectoryTab.Categories -> GenreListContent(
                        state = state.screenState,
                        genres = genres,
                        actions = actions
                    )
                }
            }
        }
    }
}

@Composable
private fun Tabs(
    selectedTab: StoreDirectoryTab,
    onTabSelected: (StoreDirectoryTab) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        backgroundColor = Color.Transparent
    )
    {
        StoreDirectoryTab.values().forEachIndexed { index, tab ->
            Tab(
                selected = (index == selectedTab.ordinal),
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = stringResource(id = tab.titleId),
                        style = MaterialTheme.typography.body2
                    )
                }
            )
        }
    }
}

@Composable
private fun TopChartsTabContent (
    state: ScreenState,
    selectedChartTab: StoreChartTab,
    onChartSelected: (StoreChartTab) -> Unit,
    storeDirectory: StoreDirectory?,
) {
    state
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
            storeDirectory?.let { directory ->
                Column {
                    TabRow(
                        selectedTabIndex = selectedChartTab.ordinal,
                        backgroundColor = Color.Transparent
                    )
                    {
                        StoreChartTab.values().forEachIndexed { index, tab ->
                            Tab(
                                selected = (index == selectedChartTab.ordinal),
                                onClick = { onChartSelected(tab) },
                                text = {
                                    Text(
                                        text = stringResource(id = tab.titleId),
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                            )
                        }
                    }
                    Surface(modifier = Modifier.weight(0.5f)) {
                        when (selectedChartTab) {
                            StoreChartTab.Podcasts ->
                                TopChartContent(
                                    topChart = directory.topPodcastsChart,
                                    storePage = directory
                                )
                            StoreChartTab.Episodes ->
                                TopChartContent(
                                    topChart = directory.topEpisodesChart,
                                    storePage = directory
                                )
                        }
                    }
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
            val primaryColor = colors[1]
            LazyGridFor(items = genres, columns = 2) { genre, idx ->
                val dominantColor = remember(genre) { mutableStateOf(primaryColor) }
                FetchDominantColorFromPoster(posterUrl = genre.artwork.url, colorState = dominantColor)
                Card(
                    backgroundColor = animate(target = dominantColor.value),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .preferredHeight(100.dp)
                        .clickable(onClick = {
                            actions.navigateToStoreGenre(genre)
                        })
                )
                {
                    Row {
                        Text(text = genre.name)
                        CoilImage(
                            imageModel = genre.artwork.url,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .preferredSize(100.dp))

                    }
                }
            }
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
