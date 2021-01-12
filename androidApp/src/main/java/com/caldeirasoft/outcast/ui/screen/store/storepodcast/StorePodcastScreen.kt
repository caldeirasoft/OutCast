package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.viewModel
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage
import com.caldeirasoft.outcast.ui.components.ErrorScreen
import com.caldeirasoft.outcast.ui.components.LoadingScreen
import com.caldeirasoft.outcast.ui.components.StoreEpisodeListItem
import com.caldeirasoft.outcast.ui.util.onError
import com.caldeirasoft.outcast.ui.util.onLoading
import com.caldeirasoft.outcast.ui.util.onSuccess
import com.caldeirasoft.outcast.ui.util.viewModelProviderFactoryOf
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun StorePodcastScreen(
    url: String,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit
)
{
    val viewModel: StorePodcastViewModel = viewModel(
        key = url,
        factory = viewModelProviderFactoryOf { StorePodcastViewModel(url) }
    )
    val viewState by viewModel.state.collectAsState()

    viewState
        .screenState
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
            val storeData = viewState.storeData
            storeData?.let {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = storeData.name)
                            },
                            navigationIcon = {
                                IconButton(onClick = navigateUp) {
                                    Icon(Icons.Filled.ArrowBack)
                                }
                            },
                            actions = {
                                IconButton(onClick = navigateUp) {
                                    Icon(imageVector = Icons.Filled.HourglassFull)
                                }
                            })
                    }
                )
                {
                    StorePodcastContent(viewModel = viewModel, storePodcastData = storeData)
                }
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

