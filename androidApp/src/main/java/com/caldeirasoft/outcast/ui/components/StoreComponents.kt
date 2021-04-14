package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg.Companion.toEpisodeArg
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastArg.Companion.toStorePodcastArg
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.util.ScreenFn
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@Composable
fun StoreCollectionItemsContent(
    storeCollection: StoreCollectionItems,
    navigateTo: ScreenFn,
    followingStatus: Map<Long, FollowStatus> = mapOf(),
    onSubscribeClick: (StorePodcast) -> Unit = { },
) {
    StoreHeadingSectionWithLink(
        title = storeCollection.label,
        onClick = {
            navigateTo(Screen.Discover(storeCollection.room))
        })

    // content
    LazyRow(
        contentPadding = PaddingValues(start = 16.dp,
            end = 16.dp,
            bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(items = storeCollection.items) { index, item ->
            when (item) {
                is StorePodcast ->
                    PodcastGridItem(
                        modifier = Modifier
                            .width(150.dp)
                            .clickable(onClick = {
                                navigateTo(Screen.StorePodcastScreen(item.toStorePodcastArg()))
                            }),
                        podcast = item,
                        index = if (storeCollection.sortByPopularity) index + 1 else null,
                        followingStatus = followingStatus[item.id],
                        onSubscribeClick = onSubscribeClick
                    )
                is StoreEpisode -> {
                    EpisodeGridItem(
                        modifier = Modifier
                            .width(180.dp)
                            .clickable(onClick = {
                                navigateTo(Screen.EpisodeScreen(item.toEpisodeArg()))
                            }),
                        episode = item.episode,
                    )
                }
            }
        }
    }
}


@Composable
fun StoreCollectionDataContent(
    storeCollection: StoreCollectionData,
    navigateTo: (Screen) -> Unit,
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
                is StoreData -> {
                    Card(
                        backgroundColor = colors[0],
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .width(200.dp)
                            .clickable(onClick = { navigateTo(Screen.Discover(item)) })
                    )
                    {
                        CoilImage(
                            data = item.getArtworkUrl(),
                            contentDescription = item.label,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(18 / 9f)
                        )
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
                            is StoreData -> navigateTo(Screen.Discover(item))
                            is StorePodcast -> navigateTo(Screen.StorePodcastScreen(item.toStorePodcastArg()))
                            is StoreEpisode -> navigateTo(Screen.EpisodeScreen(item.toEpisodeArg()))
                        }
                    }
            )
            {
                CoilImage(
                    data = item.getArtworkFeaturedUrl(),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
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
