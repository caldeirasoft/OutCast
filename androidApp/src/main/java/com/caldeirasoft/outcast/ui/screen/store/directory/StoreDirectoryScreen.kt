package com.caldeirasoft.outcast.ui.screen.store

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.util.checkType
import com.caldeirasoft.outcast.domain.util.tryCast
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreDirectoryViewModel
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

enum class StoreGenreTab(val genreId: Int, val titleId: Int) {
    Arts(1301, R.string.store_genre_1301),
    Business(1321, R.string.store_genre_1321),
    Comedy(1303, R.string.store_genre_1303),
    Education(1304, R.string.store_genre_1304),
    Fiction(1483, R.string.store_genre_1483),
    Government(1511, R.string.store_genre_1511),
    Health_Fitness(1512, R.string.store_genre_1512),
    History(1487, R.string.store_genre_1487),
    Kids_Family(1305, R.string.store_genre_1305),
    Leisure(1502, R.string.store_genre_1502),
    Music(1310, R.string.store_genre_1310),
    News(1489, R.string.store_genre_1489),
    Religion_Spirtuality(1314, R.string.store_genre_1314),
    Science(1533, R.string.store_genre_1533),
    Society_Culture(1324, R.string.store_genre_1324),
    Sports(1545, R.string.store_genre_1545),
    TV_Film(1309, R.string.store_genre_1309),
    Technology(1318, R.string.store_genre_1318),
    True_Crime(1488, R.string.store_genre_1488)
}


private val AmbientDominantColor = ambientOf<MutableState<Color>> { error("No dominant color") }

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreDirectoryScreen() {
    val actions = ActionsAmbient.current
    val viewModel: StoreDirectoryViewModel = viewModel()
    val viewState by viewModel.state.collectAsState()

    Log.d("Compose", "Compose StoreDirectoryScreen : ${Clock.System.now()}")

    StoreDirectoryContent(
        state = viewState,
        discover = viewModel.discover,
        onGenreSelected = viewModel::onGenreDiscoverSelected,
        actions = actions
    )
}

@Composable
private fun StoreDirectoryContent(
    state: StoreDirectoryViewModel.State,
    discover: Flow<PagingData<StoreItem>>,
    onGenreSelected: (Int) -> Unit,
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
        DiscoverContent(
            state = state.screenState,
            selectedGenre = state.selectedGenre,
            onGenreSelected = onGenreSelected,
            discover = discover,
            actions = actions
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiscoverContent(
    state: ScreenState,
    selectedGenre: Int?,
    onGenreSelected: (Int) -> Unit,
    discover: Flow<PagingData<StoreItem>>,
    actions: Actions)
{
    state
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
            val lazyPagingItems = discover.collectAsLazyPagingItems()
            val loadState = lazyPagingItems.loadState
            val refreshState = loadState.refresh
            LazyColumn {
                item {
                    GenreTabs(
                        selectedGenre = selectedGenre,
                        onGenreSelected = onGenreSelected
                    )
                }

                when {
                    refreshState is LoadState.Loading -> {
                        item { LoadingScreen() }
                    }
                    refreshState is LoadState.Error -> {
                        item {
                            ErrorScreen(t = refreshState.error)
                        }
                    }
                    refreshState is LoadState.NotLoading
                            && loadState.append.endOfPaginationReached
                            && lazyPagingItems.itemCount == 0 -> {
                        item {
                            Text("Empty")
                        }
                    }
                    refreshState is LoadState.NotLoading ->
                        itemsIndexed(lazyPagingItems = lazyPagingItems) { index, item ->
                            item?.let {
                                when (item) {
                                    is StorePodcast -> {
                                        StorePodcastListItem(podcast = item)
                                        Divider()
                                    }
                                    is StoreCollectionPodcasts ->
                                        StoreCollectionPodcastsContent(storeCollection = item)
                                    is StoreCollectionEpisodes ->
                                        StoreCollectionEpisodesContent(storeCollection = item)
                                    is StoreCollectionCharts ->
                                        StoreCollectionChartsContent(storeCollection = item)
                                    is StoreCollectionRooms ->
                                        StoreCollectionRoomsContent(storeCollection = item)
                                    is StoreCollectionFeatured ->
                                        StoreCollectionFeaturedContent(storeCollection = item)
                                }
                            }
                        }
                }

                when (val appendState = loadState.append) {
                    is LoadState.Loading -> {
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
                    is LoadState.Error -> {
                        item {
                            Text(
                                modifier = Modifier.padding(
                                    vertical = 16.dp,
                                    horizontal = 4.dp
                                ),
                                text = "Error getting next: ${appendState.error}"
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
                FetchDominantColorFromPoster(posterUrl = genre.url, colorState = dominantColor)
                Card(
                    backgroundColor = animate(target = dominantColor.value),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .preferredHeight(100.dp)
                        .clickable(onClick = {
                            //actions.navigateToStoreGenre(genre)
                        })
                )
                {
                    Row {
                        Text(text = genre.name)
                        CoilImage(
                            imageModel = genre.url,
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
    selectedGenre: Int?,
    onGenreSelected: (Int) -> Unit,
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
                selectedGenre
                    ?.let { MaterialTheme.colors.primary }
                    ?: MaterialTheme.colors.onSurface.copy(alpha = 0.48f)
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row {
                StoreGenreTab
                    .values()
                    //.sortedBy { stringResource(id = it.titleId) }
                    .forEachIndexed { index, category ->
                        AnimatedVisibility(
                            visible = selectedGenre == null || selectedGenre == category.genreId,
                            enter = expandHorizontally(animSpec = tween(durationMillis = 250))
                                    + fadeIn(animSpec = tween(durationMillis = 250)),
                            exit = shrinkHorizontally(animSpec = tween(durationMillis = 250))
                                    + fadeOut(animSpec = tween(durationMillis = 250)),
                        ) {
                            ChoiceChipContent(
                                text = stringResource(id = category.titleId),
                                selected = selectedGenre == category.genreId,
                                onGenreClick = {
                                    onGenreSelected(category.genreId)
                                    scrollState.smoothScrollTo(0f)
                                }
                            )

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
