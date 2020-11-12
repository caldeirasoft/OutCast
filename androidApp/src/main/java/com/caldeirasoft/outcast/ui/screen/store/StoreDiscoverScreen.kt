package com.caldeirasoft.outcast.ui.screen.store

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedTask
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.components.StorePodcastItem
import com.caldeirasoft.outcast.ui.theme.OutCastTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.java.KoinJavaComponent.inject

@Composable
fun StoreDirectoryScreen(navController: NavController) {
    val model: StoreDirectoryViewModel by inject(StoreDirectoryViewModel::class.java)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Discover")
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("inbox")
                    }) {
                        Icon(asset = Icons.Filled.HourglassFull)
                    }
                })
        }
    )
    { innerPadding ->
        StoreDirectoryContent(
            viewModel = model,
            modifier = Modifier.padding(innerPadding))
    }
}

@ExperimentalCoroutinesApi
@Composable
fun StoreDirectoryContent(
    viewModel: StoreDirectoryViewModel,
    modifier: Modifier = Modifier
) {
    val storeDataGrouping by viewModel.storeDataGroupingState.collectAsState(
        initial = Resource.Loading<StoreDataGrouping>(null)
    )
    when(storeDataGrouping) {
        is Resource.Loading<*> -> CenteredCircularProgress()
        is Resource.Success -> {
            Column(modifier = modifier.padding(16.dp)) {
                Text("Hi there!")
                Text("Thanks for going through the Layouts codelab")
                (storeDataGrouping as Resource.Success).data.storeList.forEach {
                    when (it) {
                        is StoreCollectionPodcasts -> {
                            Text(it.label)
                            if (it.items.isEmpty())
                                Text("loading")
                            else
                                Row(modifier = Modifier.preferredHeight(100.dp).fillMaxWidth()) {
                                    it.items.filterIsInstance<StoreItemPodcast>()
                                        .forEach { podcast ->
                                            StorePodcastItem(
                                                podcastTitle = podcast.name,
                                                podcastArtist = podcast.artistName,
                                                podcastImageUrl = podcast.getArtworkUrl())
                                        }
                                }
                        }
                        is StoreCollectionRooms ->
                            Text(it.label)
                        is StoreCollectionFeatured ->
                            Text("Featured")
                    }

                }
            }
        }
        else -> {
            Column(modifier = modifier.padding(16.dp)) {
                Text("Error!")
            }
        }
    }
}

@Composable
fun CenteredCircularProgress() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) { CircularProgressIndicator() }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String
) {
    LaunchedTask {
        snackbarHostState.showSnackbar(message = message)
    }
}