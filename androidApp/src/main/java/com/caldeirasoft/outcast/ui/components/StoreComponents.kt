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
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.util.ScreenFn
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun StoreCollectionPodcastsContent(
    storeCollection: StoreCollectionPodcasts,
    navigateTo: ScreenFn,
) {
    // content
    LazyRow(
        contentPadding = PaddingValues(start = 16.dp,
            end = 0.dp,
            bottom = 16.dp)
    ) {
        items(items = storeCollection.items) { item ->
            PodcastGridItem(
                modifier = Modifier
                    .width(100.dp)
                    .clickable(onClick = {
                        navigateTo(Screen.PodcastScreen(item))
                    }),
                podcast = item)
        }
    }
}

@Composable
fun StoreCollectionEpisodesContent(
    storeCollection: StoreCollectionEpisodes,
    numRows: Int,
    navigateTo: (Screen) -> Unit,
) {
    val indexedItems =
        storeCollection.items.mapIndexed { index, storeItem -> Pair(index, storeItem) }
    val chunkedItems = indexedItems.chunked(numRows)
    val pagerState: PagerState = remember { PagerState(pages = chunkedItems.size - 1) }
    val selectedPage = remember { mutableStateOf(0) }

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
                        onPodcastClick = { navigateTo(Screen.PodcastScreen(storeItem.podcast)) },
                        onEpisodeClick = { navigateTo(Screen.EpisodeScreen(storeItem.toEpisodeArg())) },
                        //index = index + 1
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
)
{
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


@Composable
fun StoreCollectionFeaturedContent(
    storeCollection: StoreCollectionFeatured,
    navigateTo: (Screen) -> Unit
) {
    val pagerState: PagerState = remember { PagerState(pages = storeCollection.items.size - 1)}
    val selectedPage = remember { mutableStateOf(0) }

    Column {
        Pager(
            state = pagerState, modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2.03f)
        )
        {
            val item = storeCollection.items[Math.floorMod(this.page, storeCollection.items.size)]
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
                            .fillMaxSize()
                    )

                    /*
                    if (item is StorePodcastFeatured && false) {
                        ConstraintLayout(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                .align(Alignment.BottomCenter)
                        ) {
                            val (name, artist, icon) = createRefs()
                            Text(
                                item.name,
                                modifier = Modifier
                                    .constrainAs(name) {
                                        linkTo(top = parent.top, bottom = artist.top, bias = 1f)
                                        linkTo(start = parent.start, end = icon.start)
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
                                        linkTo(start = parent.start, end = icon.start)
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
                    */
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
fun StoreCollectionTopPodcastsContent(
    storeCollection: StoreCollectionTopPodcasts,
    numRows: Int,
    navigateTo: (Screen) -> Unit
) {
    val indexedItems =
        storeCollection.items.mapIndexed { index, storeItem -> Pair(index, storeItem) }
    val chunkedItems = indexedItems.chunked(numRows)
    val pagerState: PagerState = remember { PagerState(pages = chunkedItems.size - 1)}
    val selectedPage = remember { mutableStateOf(0) }

    Pager(
        state = pagerState,
        //contentAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
    )
    {
        val chartItems = chunkedItems[this.currentPage]
        selectedPage.value = pagerState.currentPage

        Column(
            modifier = Modifier
                .fillMaxWidth(0.90f)
            //.padding(horizontal = 4.dp)
        )
        {
            chartItems.forEach { (index, storeItem) ->
                SmallPodcastListItemIndexed(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            navigateTo(Screen.PodcastScreen(storeItem))
                        }),
                    storePodcast = storeItem,
                    index = index + 1
                )
            }
        }
    }
}

@Composable
fun StoreCollectionTopEpisodesContent(
    storeCollection: StoreCollectionTopEpisodes,
    navigateTo: (Screen) -> Unit
) {
    // content
    LazyRow(
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 0.dp,
            bottom = 16.dp)
    ) {
        itemsIndexed(items = storeCollection.items) { index, item ->
            StoreEpisodeItem(
                modifier = Modifier.width(320.dp),
                onPodcastClick = { navigateTo(Screen.PodcastScreen(item.podcast)) },
                onEpisodeClick = { navigateTo(Screen.EpisodeScreen(item.toEpisodeArg())) },
                episode = item.episode,
                index = index + 1
            )
            Spacer(modifier = Modifier.width(16.dp))
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
