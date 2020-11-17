package com.caldeirasoft.outcast.ui.screen.store

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedTask
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.interfaces.StoreItemFeatured
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.ui.components.StoreCollectionPodcastsContent
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.util.ScreenState
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun StoreDirectoryScreen(
    viewModel: StoreDirectoryViewModel,
    navigateToStoreEntry: (String) -> Unit,
    navigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Discover")
                },
                actions = {
                    IconButton(onClick = navigateUp) {
                        Icon(asset = Icons.Filled.HourglassFull)
                    }
                })
        }
    )
    { innerPadding ->
        StoreDirectoryContent(
            viewModel = viewModel,
            navigateToStoreEntry = navigateToStoreEntry,
            modifier = Modifier.padding(innerPadding))
    }
}

@ExperimentalCoroutinesApi
@Composable
fun StoreDirectoryContent(
    viewModel: StoreDirectoryViewModel,
    navigateToStoreEntry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val storeDataGrouping = viewModel.storeGroupingData
    when (val state = viewModel.screenState) {
        is ScreenState.Loading ->
            CenteredCircularProgress()
        is ScreenState.Success ->
            storeDataGrouping?.let {
                LazyColumnFor(items = it.storeList) { collection ->
                    when (collection) {
                        is StoreCollectionPodcasts ->
                            StoreCollectionPodcastsContent(
                                storeCollection = collection,
                                navigateToStoreEntry = navigateToStoreEntry
                            )
                        is StoreCollectionRooms ->
                            StoreCollectionRoomsContent(
                                storeCollection = collection,
                                navigateToStoreEntry = navigateToStoreEntry,
                            )
                        is StoreCollectionFeatured ->
                            StoreCollectionFeaturedContent(
                                storeCollection = collection,
                                navigateToStoreEntry = navigateToStoreEntry
                            )
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
fun StoreCollectionRoomsContent(
    storeCollection: StoreCollectionRooms,
    navigateToStoreEntry: (String) -> Unit)
{
    Text(storeCollection.label, modifier = Modifier.padding(horizontal = 16.dp))
    LazyRowFor(items = storeCollection.items.filterIsInstance<StoreRoom>(),
            modifier = Modifier
                .padding(8.dp))
    { room ->
        Card(
            backgroundColor = colors[0],
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .preferredWidth(200.dp)
                .clickable(onClick = { navigateToStoreEntry(room.url) })
        )
        {
            CoilImage(
                imageModel = room.getArtworkUrl(),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(18/9f))
        }
    }
}

@Composable
fun StoreCollectionFeaturedContent(
    storeCollection: StoreCollectionFeatured,
    navigateToStoreEntry: (String) -> Unit) {
    LazyRowFor(
        items = storeCollection.items.filterIsInstance<StoreItemFeatured>(),
        modifier = Modifier
            .padding(8.dp)
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