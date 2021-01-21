package com.caldeirasoft.outcast.ui.screen.store

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreCollectionTopChart
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreDirectoryViewModel
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.px
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

private val AmbientDominantColor = ambientOf<MutableState<Color>> { error("No dominant color") }

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

@ExperimentalAnimationApi
@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreDirectoryScreen(
    navigateToCategories: (StoreCollectionGenres) -> Unit,
    navigateToGenre: (Int, String) -> Unit,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToTopCharts: (Int, StoreItemType) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit,
) {
    val viewModel: StoreDirectoryViewModel = viewModel()
    val viewState by viewModel.state.collectAsState()

    Log.d("Compose", "Compose StoreDirectoryScreen : ${Clock.System.now()}")

    StoreDirectoryContent(
        viewState = viewState,
        discover = viewModel.discover,
        navigateToCategories = navigateToCategories,
        navigateToGenre = navigateToGenre,
        navigateToRoom = navigateToRoom,
        navigateToTopCharts = navigateToTopCharts,
        navigateToPodcast = navigateToPodcast,
        navigateUp = navigateUp,
    )
}

@ExperimentalAnimationApi
@Composable
private fun StoreDirectoryContent(
    viewState: StoreDirectoryViewModel.State,
    discover: Flow<PagingData<StoreItem>>,
    navigateToCategories: (StoreCollectionGenres) -> Unit,
    navigateToGenre: (Int, String) -> Unit,
    navigateToTopCharts: (Int, StoreItemType) -> Unit,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit,
) {
    Scaffold(
        /*topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.store_tab_discover))
                },
                actions = {
                    /*IconButton(onClick = onRefresh) {
                        Icon(imageVector = Icons.Filled.Refresh)
                    }*/
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }*/
    )
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .semantics { testTag = "Store Directory screen" })
        {
            WithConstraints {
                val screenHeight = constraints.maxHeight
                val headerHeight = screenHeight / 3
                val spacerHeight = headerHeight - 56.px

                val listState = rememberLazyListState(0)
                val lazyPagingItems = discover.collectAsLazyPagingItems()
                val loadState = lazyPagingItems.loadState
                val refreshState = loadState.refresh

                LazyColumn(state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 56.dp)) {
                    item {
                        with(AmbientDensity.current) {
                            Spacer(modifier = Modifier.height(spacerHeight.toDp()))
                        }
                    }

                    item {
                        SearchBar()
                    }

                    when {
                        refreshState is LoadState.Loading -> {
                            item {
                                ShimmerStoreCollectionsList()
                            }
                        }
                        refreshState is LoadState.Error -> {
                            item {
                                ErrorScreen(t = refreshState.error)
                            }
                        }
                        refreshState is LoadState.NotLoading ->
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
                                    is StoreCollectionRooms ->
                                        StoreCollectionRoomsContent(
                                            storeCollection = item,
                                            navigateToRoom = navigateToRoom
                                        )
                                    is StoreCollectionFeatured ->
                                        StoreCollectionFeaturedContent(
                                            storeCollection = item
                                        )
                                    is StoreCollectionGenres ->
                                        StoreCollectionGenresContent(
                                            storeCollection = item,
                                            navigateToGenre = navigateToGenre,
                                            navigateToCategories = navigateToCategories
                                        )
                                    is StoreCollectionTopPodcasts ->
                                        StoreCollectionTopChartsContent(
                                            storeCollection = item,
                                            numRows = 4,
                                            navigateToTopCharts = {
                                                navigateToTopCharts(item.genreId,
                                                    StoreItemType.PODCAST)
                                            }
                                        ) { index, storeItem ->
                                            SmallPodcastListItemIndexed(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable(onClick = {
                                                        navigateToPodcast(storeItem.url)
                                                    }),
                                                storePodcast = storeItem,
                                                index = index + 1
                                            )
                                        }
                                    is StoreCollectionTopEpisodes ->
                                        StoreCollectionTopChartsContent(
                                            storeCollection = item,
                                            numRows = 3,
                                            navigateToTopCharts = {
                                                navigateToTopCharts(item.genreId,
                                                    StoreItemType.EPISODE)
                                            }
                                        ) { index, storeItem ->
                                            StoreEpisodeCardItemFromCharts(
                                                onEpisodeClick = { navigateToPodcast(storeItem.podcastEpisodeWebsiteUrl.orEmpty()) },
                                                onThumbnailClick = { navigateToPodcast(storeItem.podcastEpisodeWebsiteUrl.orEmpty()) },
                                                storeEpisode = storeItem,
                                                index = index + 1
                                            )
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
                    }
                }

                StoreDirectoryHeader(state = listState, headerHeight = headerHeight)
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun StoreDirectoryHeader(state: LazyListState, headerHeight: Int) {
    val title = stringResource(id = R.string.store_tab_discover)
    with(AmbientDensity.current) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight.toDp()))
        {
            val scrollAlpha =
                if (headerHeight != 0)
                    ((headerHeight - (state.firstVisibleItemIndex * headerHeight.toFloat() + state.firstVisibleItemScrollOffset)) / headerHeight)
                        .coerceAtLeast(0f)
                else 1f
            val minimumHeight = 56.dp
            val computedHeight =
                (scrollAlpha * headerHeight).toDp().coerceAtLeast(minimumHeight)
            Box(modifier = Modifier
                .fillMaxWidth()
                .preferredHeightIn(max = computedHeight)
                .height(computedHeight)
                .background(MaterialTheme.colors.background)) {
                Text(title,
                    style = typography.h4,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .alpha(scrollAlpha)
                )

                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .alpha(1 - scrollAlpha),
                    title = { Text(title) },
                    actions = {
                        AnimatedVisibility(visible = state.firstVisibleItemIndex > 1) {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(Icons.Default.Search)
                            }
                        }
                    },
                    backgroundColor = Color.Transparent,
                    elevation = if (state.firstVisibleItemIndex > 0) 1.dp else 0.dp
                )
            }

        }
    }
}



@Composable
private fun <T: StoreItem> StoreCollectionTopChartsContent(
    storeCollection: StoreCollectionTopChart<T>,
    numRows: Int = 4,
    navigateToTopCharts: () -> Unit,
    itemContent: @Composable (Int, T) -> Unit,
) {
    val indexedItems =
        storeCollection.storeList.mapIndexed { index, storeItem -> Pair(index, storeItem) }
    val chunkedItems = indexedItems.chunked(numRows)
    val pagerState: PagerState = run {
        val clock = AmbientAnimationClock.current
        remember(clock) { PagerState(clock, 0, 0, chunkedItems.size - 1) }
    }
    val selectedPage = remember { mutableStateOf(0) }

    Column {
        StoreHeadingSectionWithLink(
            title = storeCollection.label,
            onClick = navigateToTopCharts
        )

        Pager(
            state = pagerState,
            offscreenLimit = 2,
            contentAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            val chartItems = chunkedItems[page]
            selectedPage.value = pagerState.currentPage

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                //.padding(horizontal = 4.dp)
            )
            {
                chartItems.forEach { storeItemPair ->
                    itemContent(storeItemPair.first, storeItemPair.second)
                    Spacer(modifier = Modifier.preferredHeight(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun GenreTabs(
    genres: List<Genre>,
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
                genres
                    //.sortedBy { stringResource(id = it.titleId) }
                    .forEachIndexed { index, category ->
                        AnimatedVisibility(
                            visible = selectedGenre == null || selectedGenre == category.id,
                            enter = expandHorizontally(animSpec = tween(durationMillis = 250))
                                    + fadeIn(animSpec = tween(durationMillis = 250)),
                            exit = shrinkHorizontally(animSpec = tween(durationMillis = 250))
                                    + fadeOut(animSpec = tween(durationMillis = 250)),
                        ) {
                            ChoiceChipContent(
                                text = category.name,
                                selected = selectedGenre == category.id,
                                onGenreClick = {
                                    onGenreSelected(category.id)
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
private fun SearchBar()
{
    // search button
    OutlinedButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.medium),
            disabledContentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.disabled)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Filled.Search)
            Text("Search", modifier = Modifier.padding(horizontal = 4.dp))
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

