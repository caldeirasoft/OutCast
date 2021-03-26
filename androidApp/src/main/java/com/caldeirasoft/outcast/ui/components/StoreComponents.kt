package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg.Companion.toEpisodeArg
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.util.ScreenFn
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun StoreCollectionPodcastsContent(
    storeCollection: StoreCollectionPodcasts,
    navigateTo: ScreenFn,
    onHeaderLinkClick: () -> Unit = { navigateTo(Screen.Room(storeCollection.room)) },
    showIndex: Boolean = false,
    followingStatus: Map<Long, FollowStatus> = mapOf(),
    onSubscribeClick: (StorePodcast) -> Unit = { },
) {
    StoreHeadingSectionWithLink(
        title = storeCollection.label,
        onClick = onHeaderLinkClick
    )

    // content
    LazyRow(
        contentPadding = PaddingValues(start = 16.dp,
            end = 0.dp,
            bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(items = storeCollection.items) { index, item ->
            PodcastGridItem(
                modifier = Modifier
                    .width(150.dp)
                    .clickable(onClick = {
                        navigateTo(Screen.StorePodcastScreen(item))
                    }),
                podcast = item,
                index = if (storeCollection.sortByPopularity) index + 1 else null,
                followingStatus = followingStatus[item.id],
                onSubscribeClick = onSubscribeClick
            )
        }
    }
}

@Composable
fun StoreCollectionEpisodesContent(
    storeCollection: StoreCollectionEpisodes,
    numRows: Int,
    navigateTo: (Screen) -> Unit,
    onHeaderLinkClick: () -> Unit = { navigateTo(Screen.Room(storeCollection.room)) },
    showIndex: Boolean = false,
) {
    val indexedItems =
        storeCollection.items.mapIndexed { index, storeItem -> Pair(index, storeItem) }
    val chunkedItems = indexedItems.chunked(numRows)
    val pagerState: PagerState = remember { PagerState(pages = chunkedItems.size - 1) }
    val selectedPage = remember { mutableStateOf(0) }

    // header
    StoreHeadingSectionWithLink(
        title = storeCollection.label,
        onClick = onHeaderLinkClick
    )

    Pager(
        state = pagerState,
        contentAlignment = Alignment.Start,
        offscreenLimit = 2,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    )
    {
        if (chunkedItems.indices.contains(this.page)) {
            val chartItems = chunkedItems[Math.floorMod(this.page, chunkedItems.size)]
            selectedPage.value = pagerState.currentPage

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(280.dp)
                //.padding(horizontal = 4.dp)
            )
            {
                chartItems.forEach { (index, storeItem) ->
                    StoreEpisodeItem(
                        episode = storeItem.episode,
                        modifier = Modifier.fillMaxWidth(),
                        onPodcastClick = { navigateTo(Screen.StorePodcastScreen(storeItem.podcast)) },
                        onEpisodeClick = { navigateTo(Screen.EpisodeScreen(storeItem.toEpisodeArg())) },
                        index = if (storeCollection.sortByPopularity) (index + 1) else null
                    )
                }
            }
        }
    }
}

@Composable
fun StoreRoomItem(
    room: StoreRoom,
    navigateTo: (Screen) -> Unit,
) {
    Card(
        backgroundColor = colors[0],
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .width(200.dp)
            .clickable(onClick = {
                navigateTo(Screen.Room(room))
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
}

@Composable
fun StoreCollectionRoomsContent(
    storeCollection: StoreCollectionRooms,
    navigateTo: (Screen) -> Unit
) {
    // header
    StoreHeadingSection(title = storeCollection.label)

    // room content
    LazyRow(
        contentPadding = PaddingValues(start = 8.dp,
            end = 8.dp,
            bottom = 16.dp)
    ) {
        items(items = storeCollection.items) { item ->
            when (item) {
                is StoreRoom -> {
                    StoreRoomItem(room = item, navigateTo = navigateTo)
                }
            }
        }
    }
}

@Composable
fun StoreCollectionGenresContent(
    storeCollection: StoreCollectionGenres,
    navigateTo: (Screen) -> Unit
)
{
    val columns = 4
    val maxLines = 2
    val genreMore = Genre(-1, "More", "")
    val genresToDisplay =
        storeCollection.genres.let {
            if (it.size > columns * maxLines) it.take(columns * maxLines - 1).plus(genreMore)
            else it
        }
    Column(modifier = Modifier.fillMaxWidth()) {
        // header
        StoreHeadingSectionWithLink(
            title = storeCollection.label,
            onClick = { navigateTo(Screen.StoreCategories(storeCollection)) }
        )

        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            border = ButtonDefaults.outlinedBorder,
            shape = RoundedCornerShape(8.dp)) {

            Column(modifier = Modifier.fillMaxWidth()) {
                Grid(
                    mainAxisSpacing = 0.dp,
                    contentPadding = PaddingValues(0.dp),
                    items = genresToDisplay,
                    columns = columns,
                    rowHeight = 96.dp
                ) { item, _ ->
                    when (item.id) {
                        -1 ->
                            GenreGridItemMore(
                                genre = item,
                                howManyMore = storeCollection.genres.size - (columns * maxLines - 1),
                                onGenreClick = { navigateTo(Screen.StoreCategories(storeCollection)) })
                        else ->
                            GenreGridItem(
                                genre = item,
                                onGenreClick = { navigateTo(Screen.GenreScreen(item)) })
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun StoreCollectionFeaturedContent(
    storeCollection: StoreCollectionFeatured,
    navigateTo: (Screen) -> Unit
) {
    // Remember a PagerState with our tab count
    val pagerState = rememberPagerState(pageCount = storeCollection.items.size)

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2.03f)
        ) { page ->
            val item = storeCollection.items[Math.floorMod(page, storeCollection.items.size)]
            val bgDominantColor = Color.getColor(item.artwork?.bgColor!!)
            Card(
                backgroundColor = bgDominantColor,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxSize(0.95f)
                    .padding(horizontal = 4.dp)
                    .clickable {
                        when (item) {
                            is StoreRoom -> navigateTo(Screen.Room(item))
                            is StorePodcast -> navigateTo(Screen.StorePodcastScreen(item))
                            is StoreEpisode -> navigateTo(Screen.EpisodeScreen(item.toEpisodeArg()))
                        }
                    }
            )
            {
                CoilImage(
                    imageModel = item.getArtworkFeaturedUrl(),
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun CarouselDot(selected: Boolean, color: Color) {
    Icon(
        imageVector = Icons.Filled.Lens,
        contentDescription = null,
        modifier = Modifier
            .padding(4.dp)
            .size(12.dp),
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
                    backgroundColor = animateColorAsState(backgroundColor).value,
                    contentColor = animateColorAsState(contentColor).value,
                    disabledContentColor = MaterialTheme.colors.onSurface
                        .copy(alpha = ContentAlpha.disabled)
                ),
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 0.dp),
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
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
