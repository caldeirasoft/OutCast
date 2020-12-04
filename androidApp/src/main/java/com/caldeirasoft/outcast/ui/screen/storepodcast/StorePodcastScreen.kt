package com.caldeirasoft.outcast.ui.screen.storepodcast

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.util.DataState
import com.caldeirasoft.outcast.domain.util.onError
import com.caldeirasoft.outcast.domain.util.onLoading
import com.caldeirasoft.outcast.domain.util.onSuccess
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.theme.colors
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun StorePodcastScreen(
    viewModel: StorePodcastViewModel = viewModel(),
    url: String
)
{
    DisposableEffect(subject = url) {
        viewModel.loadData(url)
        onDispose {
        }
    }

    StorePodcastScreen(viewModel)
}

@ExperimentalCoroutinesApi
@Composable
fun StorePodcastScreen(viewModel: StorePodcastViewModel = viewModel())
{
    val actions = ActionsAmbient.current
    val storeData = viewModel.storeDataState.collectAsState(initial = DataState.Loading())

    storeData
        .value
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess { store ->
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = store.name)
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
            {
                StorePodcastContent(viewModel = viewModel, storePodcastData = store)
            }
        }
}

@Composable
fun StorePodcastContent(
    viewModel: StorePodcastViewModel,
    storePodcastData: StorePodcast
) {
    LazyColumnFor(items = storePodcastData.episodes) { item ->
        StoreEpisodeListItem(episode = item)
        Divider()
    }
}

