package com.caldeirasoft.outcast.ui.screen.storedata

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.ambient.StoreDataViewModelAmbient
import com.caldeirasoft.outcast.ui.components.ErrorScreen
import com.caldeirasoft.outcast.ui.components.LoadingScreen
import com.caldeirasoft.outcast.ui.components.StoreCollectionPodcastsContent
import com.caldeirasoft.outcast.ui.components.StorePodcastListItem
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.util.*
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
@Composable
fun StoreDataScreen(
    url: String)
{
    val viewModel = StoreDataViewModelAmbient.current
    val actions = ActionsAmbient.current

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
                        Icon(asset = Icons.Filled.HourglassFull)
                    }
                })
        }
    )
    { innerPadding ->
        StoreDataContent(
            viewModel = viewModel)
    }
}

@ExperimentalCoroutinesApi
@Composable
fun StoreDataContent(
    viewModel: StoreDataViewModel,
) {
    val storeDataFlow = viewModel.storeDataStateFlow
    storeDataFlow
        .collectAsState(initial = DataState.Loading())
        .value
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
            when(it) {
                is StoreRoom -> {
                    val pager = remember { viewModel.getPager(it) }
                    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()
                    LazyColumn {
                        items(lazyPagingItems = lazyPagingItems) { item ->
                            when (item) {
                                is StorePodcast -> {
                                    StorePodcastListItem(podcast = item)
                                    Divider()
                                }
                            }
                        }
                    }
                }
                is StoreMultiRoom -> {
                    val pager = remember { viewModel.getPager(it) }
                    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()
                    LazyColumn {
                        items(lazyPagingItems = lazyPagingItems) { collection ->
                            when (collection) {
                                is StoreCollectionPodcasts ->
                                    StoreCollectionPodcastsContent(storeCollection = collection)
                                is StoreCollectionEpisodes ->
                                    StoreCollectionEpisodesContent(storeCollection = collection)
                            }
                        }
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
