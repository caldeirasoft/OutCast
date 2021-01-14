package com.caldeirasoft.outcast.ui.screen.store

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreCollectionTopChart
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onError
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onLoading
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onSuccess
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreDirectoryViewModel
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
        topBar = {
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
        }
    )
    {
        viewState
            .storeResourceData
            .onLoading { ShimmerStoreCollectionsList() }
            .onError { ErrorScreen(t = it) }
            .onSuccess {
                DiscoverContent(
                    discover = discover,
                    headerContent = { SearchButton() }
                ) { _, item ->
                    when (item) {
                        is StoreCollectionPodcasts ->
                            StoreCollectionPodcastsContent(
                                storeCollection = item,
                                navigateToRoom = navigateToRoom,
                                navigateToPodcast = navigateToPodcast,
                            )
                        is StoreCollectionEpisodes ->
                            StoreCollectionEpisodesContent(
                                storeCollection = item)
                        is StoreCollectionRooms ->
                            StoreCollectionRoomsContent(
                                storeCollection = item,
                                navigateToRoom = navigateToRoom)
                        is StoreCollectionFeatured ->
                            StoreCollectionFeaturedContent(
                                storeCollection = item)
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
                                headerContent = {
                                    StoreHeadingSectionWithLink(
                                        title = item.label,
                                        onClick = {
                                            navigateToTopCharts(item.genreId, StoreItemType.PODCAST)
                                        }
                                    )
                                },
                            ) { index, storeItem ->
                                SmallPodcastListItemIndexed(
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable(onClick = { navigateToPodcast(storeItem.url) }),
                                    storePodcast = storeItem,
                                    index = index + 1)
                            }
                        is StoreCollectionTopEpisodes ->
                            StoreCollectionTopChartsContent(
                                storeCollection = item,
                                numRows = 3,
                                headerContent = {
                                    StoreHeadingSectionWithLink(
                                        title = item.label,
                                        onClick = {
                                            navigateToTopCharts(item.genreId, StoreItemType.EPISODE)
                                        }
                                    )
                                },
                            ) { index, storeItem ->
                                StoreEpisodeCardItemFromCharts(
                                    onEpisodeClick = { navigateToPodcast(storeItem.podcastEpisodeWebsiteUrl.orEmpty()) },
                                    onThumbnailClick = { navigateToPodcast(storeItem.podcastEpisodeWebsiteUrl.orEmpty()) },
                                    storeEpisode = storeItem,
                                    index = index + 1)
                            }
                    }
                }
            }
    }
}

@Composable
private fun <T: StoreItem> StoreCollectionTopChartsContent(
    storeCollection: StoreCollectionTopChart<T>,
    numRows: Int = 4,
    headerContent: @Composable (ColumnScope.() -> Unit),
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
        headerContent()

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
private fun SearchButton()
{
    // search button
    OutlinedButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
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
