package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onError
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onLoading
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onSuccess
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.util.viewModelProviderFactoryOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
@Composable
fun StoreRoomScreen(
    storeRoom: StoreRoom,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit
)
{
    val viewModel: StoreRoomViewModel = viewModel(
        key = "store_room_${storeRoom.id}",
        factory = viewModelProviderFactoryOf { StoreRoomViewModel(storeRoom) }
    )
    val viewState by viewModel.state.collectAsState()
    StoreRoomScreen(
        title = storeRoom.label,
        viewState = viewState,
        discover = viewModel.discover,
        navigateToRoom = navigateToRoom,
        navigateToPodcast = navigateToPodcast,
        navigateUp = navigateUp)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StoreRoomScreen(
    title: String,
    viewState: StoreRoomViewModel.State,
    discover: Flow<PagingData<StoreItem>>,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title)
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
        DiscoverContent(
            discover = discover,
            loadingContent = { ShimmerStoreCollectionsList() },
        ) { lazyPagingItems ->
            when (viewState.storeData) {
                is StoreMultiRoomPage ->
                    items(lazyPagingItems = lazyPagingItems) { item ->
                        when (item) {
                            is StoreCollectionPodcasts ->
                                StoreCollectionPodcastsContent(
                                    storeCollection = item,
                                    navigateToRoom = navigateToRoom,
                                    navigateToPodcast = navigateToPodcast,
                                )
                            is StoreCollectionEpisodes ->
                                StoreCollectionEpisodesContent(
                                    storeCollection = item
                                )
                            is StoreCollectionRooms ->
                                StoreCollectionRoomsContent(
                                    storeCollection = item,
                                    navigateToRoom = navigateToRoom
                                )
                        }
                    }
                is StoreRoomPage ->
                    gridItems(
                        lazyPagingItems = lazyPagingItems,
                        contentPadding = PaddingValues(16.dp),
                        horizontalInnerPadding = 8.dp,
                        verticalInnerPadding = 8.dp,
                        columns = 3
                    ) { item ->
                        if (item is StorePodcast) {
                            PodcastGridItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = { navigateToPodcast(item.url) }),
                                podcast = item
                            )
                        }
                    }
            }
        }
    }
}
