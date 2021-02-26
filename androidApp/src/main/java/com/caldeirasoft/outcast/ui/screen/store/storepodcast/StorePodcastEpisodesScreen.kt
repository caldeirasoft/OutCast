package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onLoading
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onSuccess
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetContent
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.util.viewModelProviderFactoryOf
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun StorePodcastEpisodesScreen(
    storePodcast: StorePodcast,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: StorePodcastViewModel = viewModel(
        key = storePodcast.url,
        factory = viewModelProviderFactoryOf { StorePodcastViewModel(storePodcast) }
    )
    val viewState by viewModel.state.collectAsState()

    StorePodcastEpisodesScreen(
        viewState = viewState,
        navigateTo = navigateTo,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StorePodcastEpisodesScreen(
    viewState: StorePodcastViewModel.State,
    navigateTo: (Screen) -> Unit,
    navigateBack: () ->
    Unit,
) {
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current

    val listState = rememberLazyListState(1)

    ReachableScaffold { headerHeight ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()) {

            item {
                StorePodcastExpandedHeader(
                    viewState = viewState,
                    listState = listState,
                    headerHeight = headerHeight
                )
            }

            viewState.storeResourceData
                .onLoading {
                    item {
                        ShimmerStoreCollectionsList()
                    }
                }
                .onSuccess<StorePodcastPage> { it ->
                    // recent episodes
                    item {
                        StoreHeadingSection(title = "All episodes")
                    }
                    items(it.episodes) {
                        EpisodeItem(
                            storeEpisode = it,
                            onEpisodeClick = {  })

                        Divider()
                    }

                    item {
                        // bottom app bar spacer
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                }
        }

        ReachableAppBar(
            collapsedContent = {
                StorePodcastCollapsedHeader(
                    viewState = viewState,
                    listState = listState,
                    headerHeight = headerHeight,
                    navigateUp = navigateBack)
            },
            state = listState,
            headerHeight = headerHeight)
    }
}
