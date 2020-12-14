package com.caldeirasoft.outcast.ui.screen.storepodcast

import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.viewModel
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage
import com.caldeirasoft.outcast.domain.util.DataState
import com.caldeirasoft.outcast.domain.util.onError
import com.caldeirasoft.outcast.domain.util.onLoading
import com.caldeirasoft.outcast.domain.util.onSuccess
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
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
                                Icon(imageVector = Icons.Filled.HourglassFull)
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
    storePodcastData: StorePodcastPage
) {
    LazyColumnFor(items = storePodcastData.episodes) { item ->
        StoreEpisodeListItem(episode = item)
        Divider()
    }
}

