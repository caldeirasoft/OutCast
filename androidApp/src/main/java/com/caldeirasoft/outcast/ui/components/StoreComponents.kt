package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.emptyContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.theme.getColor
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.flow.Flow



@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DiscoverContent(
    discover: Flow<PagingData<StoreItem>>,
    loadingContent: @Composable () -> Unit = emptyContent(),
    headerContent: @Composable () -> Unit = emptyContent(),
    itemsContent: LazyListScope.(LazyPagingItems<StoreItem>) -> Unit,
)
{
    val lazyPagingItems = discover.collectAsLazyPagingItems()
    val loadState = lazyPagingItems.loadState
    val refreshState = loadState.refresh
    LazyColumn {
        // header
        item {
            headerContent()
        }

        // content
        when {
            refreshState is LoadState.Loading -> {
                item {
                    loadingContent()
                }
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
                itemsContent(lazyPagingItems)
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

@Composable
fun StoreCollectionEpisodesContent(storeCollection: StoreCollectionEpisodes) {
    Text(storeCollection.label, modifier = Modifier.padding(horizontal = 16.dp))
    if (storeCollection.items.isEmpty())
        Row(
            modifier = Modifier
                .padding(8.dp)
                .preferredHeight(100.dp)
                .fillMaxWidth()
        ) {
            (1..7).forEach { _ ->
                Card(
                    backgroundColor = colors[0],
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                {
                    Spacer(modifier = Modifier.preferredSize(100.dp))
                }
            }
        }
    else
        LazyRow(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            items(items = storeCollection.items,
                itemContent = { episode ->
                    //StoreItemEpisodeContent(episode = episode)
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .preferredWidth(100.dp)
                            .clickable(onClick = {})
                    ) {
                        Card(
                            backgroundColor = colors[1],
                            shape = RoundedCornerShape(8.dp)
                        )
                        {
                            CoilImage(
                                imageModel = episode.getArtworkUrl(),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .preferredSize(100.dp)
                            )
                        }
                        Text(
                            episode.name,
                            modifier = Modifier.width(100.dp),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                            style = MaterialTheme.typography.body2
                        )
                        Text(
                            episode.podcastName,
                            modifier = Modifier.width(100.dp),
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.caption
                        )
                    }
                })
        }
}

@Composable
fun StoreCollectionPodcastsContent(
    storeCollection: StoreCollectionPodcasts,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(
            vertical = 16.dp
        )
    ) {
        StoreHeadingSectionWithLink(
            title = storeCollection.label,
            onClick = {
                navigateToRoom(
                    StoreRoom(
                        id = 0,
                        label = storeCollection.label,
                        storeIds = storeCollection.itemsIds,
                        url = storeCollection.url.orEmpty(),
                        storeFront = storeCollection.storeFront
                    )
                )
            }
        )
        Spacer(modifier = Modifier.preferredHeight(8.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items = storeCollection.items,
                itemContent = { podcast ->
                    PodcastGridItem(
                        modifier = Modifier
                            .preferredWidth(100.dp)
                            .clickable(onClick = { navigateToPodcast(podcast.url) }),
                        podcast = podcast)
                })
        }
    }
}

@Composable
fun StoreCollectionRoomsContent(
    storeCollection: StoreCollectionRooms,
    navigateToRoom: (StoreRoom) -> Unit
) {
    Column(
        modifier = Modifier.padding(
            vertical = 16.dp
        )
    ) {
        StoreHeadingSection(title = storeCollection.label)
        Spacer(modifier = Modifier.preferredHeight(8.dp))
        LazyRow(
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items = storeCollection.items.filterIsInstance<StoreRoom>(),
                itemContent = { room ->
                    Card(
                        backgroundColor = colors[0],
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .preferredWidth(200.dp)
                            .clickable(onClick = {
                                navigateToRoom(room)
                            })
                    )
                    {
                        CoilImage(
                            imageModel = room.getArtworkUrl(),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(18 / 9f)
                        )
                    }
                })
        }
    }
}

@Composable
fun StoreCollectionGenresContent(
    storeCollection: StoreCollectionGenres,
    navigateToCategories: (StoreCollectionGenres) -> Unit,
    navigateToGenre: (Int, String) -> Unit,
)
{
    val columns = 2
    val maxLines = 3
    val genreMore: StoreGenre = StoreGenre(-1, "More", "", "")
    val genresToDisplay =
        storeCollection.genres.let {
            if (it.size > columns * maxLines) it.take(columns * maxLines - 1).plus(genreMore)
            else it
        }
    Column(modifier = Modifier.fillMaxWidth()) {
        StoreHeadingSectionWithLink(title = storeCollection.label, onClick = { navigateToCategories(storeCollection) })

        Grid(
            mainAxisSpacing = 8.dp,
            contentPadding = PaddingValues(8.dp),
            items = genresToDisplay,
            columns = 2,
        ) { item, innerPadding ->
            when(item.id) {
                -1 ->
                    GenreGridItemMore(
                        storeGenre = item,
                        howManyMore = storeCollection.genres.size - (columns * maxLines - 1),
                        onGenreClick = { navigateToCategories(storeCollection) })
                else ->
                    GenreGridItem(
                        storeGenre = item,
                        onGenreClick = { navigateToGenre(item.id, item.name) })
            }
        }
    }
}


@Composable
fun StoreCollectionFeaturedContent(
    storeCollection: StoreCollectionFeatured
) {
    val pagerState: PagerState = run {
        val clock = AmbientAnimationClock.current
        remember(clock) { PagerState(clock, 0, 0, storeCollection.items.size - 1) }
    }
    val selectedPage = remember { mutableStateOf(0) }

    Column {
        Pager(
            state = pagerState, modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.53f)
        )
        {
            val item = storeCollection.items[page]
            selectedPage.value = pagerState.currentPage
            val bgDominantColor = Color.getColor(item.artwork?.bgColor!!)
            Card(
                backgroundColor = bgDominantColor,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxSize(0.95f)
                    .padding(horizontal = 4.dp)
            )
            {
                Box {
                    CoilImage(
                        imageModel = item.getArtworkUrl(),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2.03f)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3.33f)
                            .align(Alignment.BottomCenter)
                    ) {
                        WithConstraints {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(bgDominantColor.copy(alpha = 0f), bgDominantColor),
                                            startY = 0.0f,
                                            endY = constraints.maxHeight.toFloat() * 0.30f
                                        )
                                    )
                            )
                            {
                                Log_D("HEIGHT", constraints.maxHeight.toFloat().toString())
                            }

                        }
                    }
                    if (item is StorePodcastFeatured) {
                        ConstraintLayout(
                            modifier = Modifier
                                .fillMaxWidth()
                                .preferredHeight(100.dp)
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                .align(Alignment.BottomCenter)
                        ) {
                            val (thumbnail, name, artist, icon) = createRefs()
                            Card(
                                backgroundColor = Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .constrainAs(thumbnail) {
                                        linkTo(top = parent.top, bottom = parent.bottom)
                                        linkTo(start = parent.start, end = name.start)
                                        width = Dimension.value(70.dp)
                                    }
                                    .padding(end = 15.dp)
                            ) {
                                CoilImage(
                                    imageModel = item.getPodcastArtworkUrl(),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                )
                            }
                            Text(
                                item.name,
                                modifier = Modifier
                                    .constrainAs(name) {
                                        linkTo(top = parent.top, bottom = artist.top, bias = 1f)
                                        linkTo(start = thumbnail.end, end = icon.start)
                                        width = Dimension.fillToConstraints
                                        height = Dimension.wrapContent

                                    },
                                style = MaterialTheme.typography.h6,
                                color = Color.getColor(item.artwork?.textColor1!!)
                            )
                            Text(
                                item.artistName,
                                modifier = Modifier
                                    .constrainAs(artist) {
                                        linkTo(top = name.bottom, bottom = parent.bottom, bias = 1f)
                                        linkTo(start = thumbnail.end, end = icon.start)
                                        width = Dimension.fillToConstraints
                                        height = Dimension.fillToConstraints
                                    },
                                style = MaterialTheme.typography.body2,
                                color = Color.getColor(item.artwork?.textColor2!!)
                            )
                            Icon(imageVector = Icons.Filled.Add,
                                tint = Color.getColor(item.artwork?.textColor1!!),
                                modifier = Modifier
                                    .constrainAs(icon) {
                                        linkTo(top = parent.top, bottom = parent.bottom)
                                        linkTo(start = name.end, end = parent.end)
                                        width = Dimension.wrapContent
                                    })
                        }

                    }
                }
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            storeCollection.items.forEachIndexed { index, _ ->
                CarouselDot(
                    selected = index == selectedPage.value,
                    MaterialTheme.colors.primary,
                )
            }
        }
    }
}

@Composable
fun CarouselDot(selected: Boolean, color: Color) {
    Icon(
        imageVector = Icons.Filled.Lens,
        modifier = Modifier.padding(4.dp).preferredSize(12.dp),
        tint = if (selected) color else Color.Gray
    )
}

@Composable
fun ChoiceChipTab(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
) {
    val backgroundColor: Color = when {
        selected -> MaterialTheme.colors.primary.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    val contentColor: Color = when {
        selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }
    Tab(selected, onClick) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        )
        {
            OutlinedButton(
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = animate(backgroundColor),
                    contentColor = animate(contentColor),
                    disabledContentColor = MaterialTheme.colors.onSurface
                        .copy(alpha = ContentAlpha.disabled)
                ),
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 0.dp),
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(32.dp)
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        letterSpacing = 0.25.sp
                    )
                )
            }
        }
    }
}
