package com.caldeirasoft.outcast.ui.screen.storedirectory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LifecycleOwnerAmbient
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.domain.interfaces.StoreItemFeatured
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.ambient.StoreDirectoryViewModelAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.util.*
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

typealias NavigateToStoreEntryCallBack = (String) -> Unit

@ExperimentalCoroutinesApi
@Composable
fun StoreDirectoryScreen() {
    val actions = ActionsAmbient.current
    val viewModel = StoreDirectoryViewModelAmbient.current
    val lifecycleOwner = LifecycleOwnerAmbient.current

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
        StoreDirectoryContent()
    }
}

@ExperimentalCoroutinesApi
@Composable
fun StoreDirectoryContent() {
    val viewModel = StoreDirectoryViewModelAmbient.current
    val storeGroupingDataFlow = viewModel.storeGroupingDataStateFlow
    storeGroupingDataFlow
        .collectAsState(initial = DataState.Loading())
        .value
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
            val pager = remember { viewModel.getPager(it) }
            val lazyPagingItems = pager.flow.collectAsLazyPagingItems()
            LazyColumn {
                items(lazyPagingItems = lazyPagingItems) { collection ->
                    when (collection) {
                        is StoreCollectionPodcasts ->
                            StoreCollectionPodcastsContent(storeCollection = collection)
                        is StoreCollectionRooms ->
                            StoreCollectionRoomsContent(storeCollection = collection)
                        is StoreCollectionFeatured ->
                            StoreCollectionFeaturedContent(storeCollection = collection)
                    }
                }
            }
        }
}

@Composable
fun StoreCollectionRoomsContent(storeCollection: StoreCollectionRooms)
{
    val actions = ActionsAmbient.current
    Column(modifier = Modifier.padding(
        horizontal = 8.dp, vertical = 16.dp
    )) {
        StoreHeadingSection(title = storeCollection.label)
        Spacer(modifier = Modifier.preferredHeight(8.dp))
        LazyRowFor(items = storeCollection.items.filterIsInstance<StoreRoom>()) { room ->
            Card(
                backgroundColor = colors[0],
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .preferredWidth(200.dp)
                    .clickable(onClick = { actions.navigateToStoreEntry(room.url) })
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
    storeCollection: StoreCollectionFeatured) {
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