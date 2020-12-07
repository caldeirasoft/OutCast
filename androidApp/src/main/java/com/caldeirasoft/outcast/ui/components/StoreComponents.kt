package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import androidx.ui.tooling.preview.Preview
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StoreItemFeatured
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.theme.colors
import com.skydoves.landscapist.coil.CoilImage

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
        }
        itemsIndexed(lazyPagingItems = lazyPagingItems) { index, item ->
            item?.let {
                itemContent(item, index)
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
                    modifier = Modifier.padding(horizontal = 8.dp))
                {
                    Spacer(modifier = Modifier.preferredSize(100.dp))
                }
            }
        }
    else
        LazyRowFor(
            items = storeCollection.items.filterIsInstance<StoreEpisode>(),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) { episode ->
            //StoreItemEpisodeContent(episode = episode)
            Column(modifier = Modifier
                .padding(horizontal = 8.dp)
                .preferredWidth(100.dp)
                .clickable(onClick = {})) {
                Card(
                    backgroundColor = colors[1],
                    shape = RoundedCornerShape(8.dp)
                )
                {
                    CoilImage(
                        imageModel = episode.getArtworkUrl(),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .preferredSize(100.dp))
                }
                Text(
                    episode.name,
                    modifier = Modifier.width(100.dp),
                    overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.body2
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
    Column(modifier = Modifier.padding(
        vertical = 16.dp
    )) {
        StoreHeadingSectionWithLink(
            title = storeCollection.label,
            onClick = {
                actions.navigateToStoreCollection(
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
                        modifier = Modifier.padding(horizontal = 8.dp))
                    {
                        Spacer(modifier = Modifier.preferredSize(100.dp))
                    }
                }
            }
        else
            LazyRowFor(
                items = storeCollection.items.filterIsInstance<StorePodcast>(),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) { podcast ->
                StorePodcastGridItem(podcast = podcast)
            }

    }
}

@Composable
fun StoreCollectionRoomsContent(storeCollection: StoreCollectionRooms)
{
    val actions = ActionsAmbient.current
    Column(modifier = Modifier.padding(
        vertical = 16.dp
    )) {
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
                        actions.navigateToStoreCollection(room)
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
    LazyRowFor(
        items = storeCollection.items.filterIsInstance<StoreItemFeatured>(),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier
            .preferredHeight(200.dp)
            .fillMaxWidth())
    { room ->
        Card(
            backgroundColor = colors[0],
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(horizontal = 8.dp))
        {
            Spacer(modifier = Modifier.preferredHeight(200.dp).preferredWidth(250.dp))
        }
    }
}

@Preview
@Composable
fun previewStoreCollectionPodcastContent() {
    val collection = remember { StoreCollectionPodcasts("Nouveautés et tendances", itemsIds = mutableListOf(), storeFront = "") }
    StoreCollectionPodcastsContent(storeCollection = collection)
}
