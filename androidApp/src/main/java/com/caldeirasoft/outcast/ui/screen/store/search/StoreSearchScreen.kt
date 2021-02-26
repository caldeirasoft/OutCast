@file:OptIn(KoinApiExtension::class)
package com.caldeirasoft.outcast.ui.screen.store

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreDirectoryViewModel
import com.caldeirasoft.outcast.ui.util.getViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreSearchScreen(
    navigateToGenre: (Int, String) -> Unit,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToTopCharts: (Int, StoreItemType) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit,
) {
    val viewModel: StoreDirectoryViewModel by getViewModel()
    val viewState by viewModel.state.collectAsState()

    //Log.d("Compose", "Compose StoreDirectoryScreen : ${Clock.System.now()}")

    StoreDirectoryContent(
        viewState = viewState,
        discover = viewModel.discover,
        navigateToGenre = navigateToGenre,
        navigateToRoom = navigateToRoom,
        navigateToTopCharts = navigateToTopCharts,
        navigateToPodcast = navigateToPodcast,
        navigateUp = navigateUp,
    )
}

@Composable
private fun StoreDirectoryContent(
    viewState: StoreDirectoryViewModel.State,
    discover: Flow<PagingData<StoreItem>>,
    navigateToGenre: (Int, String) -> Unit,
    navigateToTopCharts: (Int, StoreItemType) -> Unit,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // search button
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colors.onSurface
                                .copy(alpha = ContentAlpha.medium),
                            disabledContentColor = MaterialTheme.colors.onSurface
                                .copy(alpha = ContentAlpha.disabled)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Filled.Search,
                                contentDescription = null,)
                            Text("Search", modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                },
                actions = {
                    /*IconButton(onClick = onRefresh) {
                        Icon(imageVector = Icons.Filled.Refresh)
                    }*/
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    )
    {
        StoreCollectionChartsContent(
            viewState = viewState,
            navigateToTopCharts = navigateToTopCharts,
            navigateToPodcast = navigateToPodcast
        )
    }
}

@Composable
private fun StoreCollectionChartsContent(
    viewState: StoreDirectoryViewModel.State,
    navigateToTopCharts: (Int, StoreItemType) -> Unit,
    navigateToPodcast: (String) -> Unit,
) {
    /*viewState
        .storeResourceData
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {

            LazyColumn {
                val storeCollectionCharts = viewState.storeCollectionCharts
                storeCollectionCharts.forEach { collection ->
                    item {
                        StoreHeadingSectionWithLink(
                            title = collection.label,
                            onClick = {
                                navigateToTopCharts(
                                    collection.genreId,
                                    collection.itemType
                                )
                            }
                        )
                    }
                    item {
                        StoreCollectionChartsContent(
                            collection.storeList,
                            navigateToPodcast = navigateToPodcast,
                        )
                    }
                }
            }
        }*/
}

