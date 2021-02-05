package com.caldeirasoft.outcast.ui.screen.store.directory

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.screen.store.categories.CategoriesListScreen
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.*
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlin.math.log10

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
    navigateToGenre: (Int, String) -> Unit,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToTopCharts: (StoreItemType) -> Unit,
    navigateToPodcast: (StorePodcast) -> Unit,
    navigateToCategories: (StoreCollectionGenres) -> Unit,
    navigateUp: () -> Unit,
) {
    val viewModel: StoreDirectoryViewModel = viewModel()
    val viewState by viewModel.state.collectAsState()

    Log.d("Compose", "Compose StoreDirectoryScreen : ${Clock.System.now()}")

    StoreDirectoryContent(
        viewState = viewState,
        discover = viewModel.discover,
        navigateToGenre = navigateToGenre,
        navigateToRoom = navigateToRoom,
        navigateToTopCharts = navigateToTopCharts,
        navigateToPodcast = navigateToPodcast,
        navigateToCategories = navigateToCategories,
        navigateUp = navigateUp,
    )
}

@ExperimentalAnimationApi
@Composable
private fun StoreDirectoryContent(
    viewState: StoreDirectoryViewModel.State,
    discover: Flow<PagingData<StoreItem>>,
    navigateToGenre: (Int, String) -> Unit,
    navigateToTopCharts: (StoreItemType) -> Unit,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (StorePodcast) -> Unit,
    navigateToCategories: (StoreCollectionGenres) -> Unit,
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
                .padding(top = 56.dp))
        {
            item {
                with(AmbientDensity.current) {
                    Spacer(modifier = Modifier.height(spacerHeight.toDp()))
                }
            }

            lazyPagingItems
                .ifLoading {
                    item {
                        ShimmerStoreCollectionsList()
                    }
                }
                .ifError {
                    item {
                        ErrorScreen(t = it)
                    }
                }
                .ifNotLoading {
                    items(lazyPagingItems = lazyPagingItems) { collection ->
                        when (collection) {
                            is StoreCollectionFeatured ->
                                StoreCollectionFeaturedContent(
                                    storeCollection = collection,
                                )
                            is StoreCollectionItems -> {
                                // header
                                StoreHeadingSectionWithLink(
                                    title = collection.label,
                                    onClick = { navigateToRoom(collection.room) }
                                )
                                // content
                                StoreCollectionItemsContent(
                                    storeCollection = collection,
                                    navigateToPodcast = navigateToPodcast,
                                    navigateToEpisode = {}
                                )
                            }
                            is StoreCollectionRooms -> {
                                // header
                                StoreHeadingSection(title = collection.label)
                                // genres
                                StoreCollectionRoomsContent(
                                    storeCollection = collection,
                                    navigateToRoom = navigateToRoom
                                )
                            }
                            is StoreCollectionGenres -> {
                                // header
                                StoreHeadingSectionWithLink(
                                    title = collection.label,
                                    onClick = { navigateToCategories(collection) }
                                )
                                // genres
                                StoreCollectionGenresContent(
                                    storeCollection = collection,
                                    navigateToGenre = navigateToGenre,
                                    navigateToCategories = navigateToCategories
                                )
                            }
                            is StoreCollectionTopPodcasts -> {
                                // header
                                StoreHeadingSectionWithLink(
                                    title = collection.label,
                                    onClick = { navigateToTopCharts(StoreItemType.PODCAST) }
                                )
                                StoreCollectionTopPodcastsContent(
                                    storeCollection = collection,
                                    numRows = 4,
                                    navigateToPodcast = navigateToPodcast)
                            }
                        }
                    }
                }
                .ifLoadingMore {
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


        ReachableAppBarWithSearchBar(
            title = {
                Text(text = stringResource(id = R.string.store_tab_discover))
            },
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Search)
                }
            },
            state = listState,
            headerHeight = headerHeight)
    }
}

@ExperimentalAnimationApi
@Composable
fun ReachableAppBarWithSearchBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    state: LazyListState,
    headerHeight: Int)
{
    with(AmbientDensity.current) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight.toDp()))
        {
            val scrollRatioLargeHeader =
                if (headerHeight != 0)
                    ((headerHeight - (state.firstVisibleItemIndex * headerHeight.toFloat() + state.firstVisibleItemScrollOffset)) / headerHeight)
                        .coerceAtLeast(0f)
                else 1f
            val minimumHeight = 56.dp
            val computedHeight = (scrollRatioLargeHeader * headerHeight).toDp().coerceAtLeast(minimumHeight)
            val alphaLargeHeader = (3 * log10(scrollRatioLargeHeader.toDouble()) + 1).toFloat().coerceIn(0f, 1f)
            val alphaCollapsedHeader = (3 * log10((1-scrollRatioLargeHeader).toDouble()) + 1).toFloat().coerceIn(0f, 1f)
            Box(modifier = Modifier
                .fillMaxWidth()
                .preferredHeightIn(max = computedHeight)
                .height(computedHeight)
                .background(MaterialTheme.colors.background)) {

                // large title
                Box(modifier = Modifier
                    .padding(bottom = (56.dp.toIntPx() * scrollRatioLargeHeader).toDp())
                    .align(Alignment.Center)
                    .alpha(alphaLargeHeader)) {
                    ProvideTextStyle(typography.h4, title)
                }

                // top app bar
                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .alpha(alphaCollapsedHeader),
                    title = {
                        Providers(AmbientContentAlpha provides alphaCollapsedHeader) {
                            title()
                        }
                    },
                    navigationIcon = navigationIcon,
                    actions = actions,
                    backgroundColor = Color.Transparent,
                    elevation = if (state.firstVisibleItemIndex > 0) 1.dp else 0.dp
                )

                // search bar
                SearchBar(modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .alpha(alphaLargeHeader))
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
private fun SearchBar(modifier: Modifier = Modifier)
{
    // search button
    OutlinedButton(
        onClick = {},
        modifier = modifier
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

