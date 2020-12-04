package com.caldeirasoft.outcast.ui.screen.storeroom

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
@Composable
fun StoreCollectionScreen(
    storeRoom: StoreRoom,
    viewModel: StoreCollectionViewModel = getViewModel { parametersOf(storeRoom) },
)
{
    val actions = ActionsAmbient.current
    val lazyPagingItems = viewModel.pagedList.collectAsLazyPagingItems()

    StoreCollectionContent(
        title = storeRoom.label,
        lazyPagingItems = lazyPagingItems,
        actions = actions)
}

@Composable
fun StoreCollectionContent(
    title: String,
    lazyPagingItems: LazyPagingItems<StoreItem>,
    actions: Actions
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title)
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
        StoreContentFeed(
            lazyPagingItems = lazyPagingItems,
            actions = actions) { item, index ->
            when (item) {
                is StorePodcast -> {
                    StorePodcastListItem(podcast = item)
                    Divider()
                }
                is StoreCollectionPodcasts ->
                    StoreCollectionPodcastsContent(storeCollection = item)
                is StoreCollectionEpisodes ->
                    StoreCollectionEpisodesContent(storeCollection = item)
                is StoreCollectionRooms ->
                    StoreCollectionRoomsContent(storeCollection = item)
            }
        }
    }
}