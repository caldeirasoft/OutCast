package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lens
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.VerticalGradient
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.skydoves.landscapist.coil.CoilImage

enum class StoreChartTab(val titleId: Int) {
    Podcasts(R.string.store_tab_chart_podcasts),
    Episodes(R.string.store_tab_chart_episodes),
}


@Composable
fun StoreContentFeed(
    lazyPagingItems: LazyPagingItems<StoreItem>,
    actions: Actions,
    itemContent: @Composable (StoreItem, Int) -> Unit
) {
    LazyColumn {
        val loadState = lazyPagingItems.loadState
        val refreshState = loadState.refresh
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
                        itemContent(item, index)
                    }
                }
        }

        when (val appendState = loadState.append) {
            is LoadState.Loading -> {
                item {
                    Text(
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 4.dp),
                        text = "Loading next"
                    )
                }
            }
            is LoadState.Error -> {
                item {
                    Text(
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 4.dp),
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
            (1..7).forEach { episode ->
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
        LazyRowFor(
            items = storeCollection.items,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) { episode ->
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
        }
}

@Composable
fun StoreCollectionPodcastsContent(storeCollection: StoreCollectionPodcasts) {
    val actions = ActionsAmbient.current
    Column(
        modifier = Modifier.padding(
            vertical = 16.dp
        )
    ) {
        StoreHeadingSectionWithLink(
            title = storeCollection.label,
            onClick = {
                actions.navigateToStoreRoom(
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
        if (storeCollection.items.isEmpty())
            Row(
                modifier = Modifier
                    .preferredHeight(100.dp)
                    .fillMaxWidth()
            ) {
                (1..2).forEach { _ ->
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
            LazyRowFor(
                items = storeCollection.items,
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) { podcast ->
                StorePodcastGridItem(podcast = podcast)
            }

    }
}

@Composable
fun StoreCollectionRoomsContent(storeCollection: StoreCollectionRooms) {
    val actions = ActionsAmbient.current
    Column(
        modifier = Modifier.padding(
            vertical = 16.dp
        )
    ) {
        StoreHeadingSection(title = storeCollection.label)
        Spacer(modifier = Modifier.preferredHeight(8.dp))
        LazyRowFor(
            items = storeCollection.items.filterIsInstance<StoreRoom>(),
            contentPadding = PaddingValues(8.dp)
        ) { room ->
            Card(
                backgroundColor = colors[0],
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .preferredWidth(200.dp)
                    .clickable(onClick = {
                        actions.navigateToStoreRoom(room)
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
                                        brush = VerticalGradient(
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
                        Text(
                            text = item.name,
                            style = typography.h6.copy(color = Color.White),
                            modifier = Modifier.fillMaxWidth().padding(24.dp)
                                .align(Alignment.BottomStart),
                        )
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
fun StoreCollectionChartsContent(storeCollection: StoreCollectionCharts) {
    val actions = ActionsAmbient.current
    var selectedChartTab: StoreChartTab by remember { mutableStateOf(StoreChartTab.Podcasts) }
    Column(
        modifier = Modifier.padding(
            vertical = 16.dp
        )
    ) {
        StoreHeadingSectionWithLink(
            title = "TODO: Charts",
            onClick = { actions.navigateToStoreCharts(
                StoreTopCharts(
                    id = storeCollection.genreId?.toLong() ?: 0L,
                    genreId = storeCollection.genreId,
                    storeFront = storeCollection.storeFront
                )
            ) }
        )
        Spacer(modifier = Modifier.preferredHeight(8.dp))
        TopChartsTabContent(
            storeCollection = storeCollection,
            selectedChartTab = selectedChartTab,
            onChartSelected = { selectedChartTab = it }
        )
    }
}

@Composable
private fun TopChartsTabContent(
    storeCollection: StoreCollectionCharts,
    selectedChartTab: StoreChartTab,
    onChartSelected: (StoreChartTab) -> Unit
) {
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
        when (selectedChartTab) {
            StoreChartTab.Podcasts ->
                TopChartContent(storeCollection.topPodcasts)
            StoreChartTab.Episodes ->
                TopChartContent(storeCollection.topEpisodes)
        }
    }
}

@Composable
private fun TopChartContent(
    topCharts: List<StoreItem>,
) {
    topCharts.forEachIndexed { index, storeItem ->
        when (storeItem) {
            is StorePodcast -> {
                StorePodcastListItemIndexed(podcast = storeItem, index = index + 1)
                Divider()
            }
            is StoreEpisode -> {
                StoreEpisodeListItem(episode = storeItem/*, index = index + 1*/)
                Divider()
            }
        }
    }
}

@Preview
@Composable
fun previewStoreCollectionPodcastContent() {
}
