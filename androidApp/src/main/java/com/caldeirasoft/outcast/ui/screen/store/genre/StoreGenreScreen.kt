package com.caldeirasoft.outcast.ui.screen.store.genre

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionEpisodes
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionFeatured
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionPodcasts
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.util.*
import kotlinx.coroutines.flow.flowOf

@Composable
fun StoreGenreScreen(
    storeGenre: StoreGenre,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: StoreGenreViewModel = mavericksViewModel(initialArgument = storeGenre)
    val state by viewModel.collectAsState()

    StoreGenreContent(
        state = state,
        navigateTo = navigateTo,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StoreGenreContent(
    state: StoreGenreViewState,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val listState = rememberLazyListState(0)
    val lazyPagingItems = flowOf(state.discover).collectAsLazyPagingItems()

    ReachableScaffold { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)) {

            item {
                Spacer(modifier = Modifier.height(spacerHeight.toDp()))
            }

            lazyPagingItems
                .ifLoading {
                    item {
                        ShimmerStoreCollectionsList()
                    }
                }
                .ifError {
                    item {
                        ErrorScreen(t = it)
                    }
                }
                .ifNotLoading {
                    items(lazyPagingItems = lazyPagingItems) { collection ->
                        when (collection) {
                            is StoreCollectionFeatured ->
                                StoreCollectionFeaturedContent(
                                    storeCollection = collection,
                                    navigateTo = navigateTo
                                )
                            is StoreCollectionPodcasts -> {
                                // header
                                StoreHeadingSectionWithLink(
                                    title = collection.label,
                                    onClick = { navigateTo(Screen.Room(collection.room)) }
                                )

                                // content
                                StoreCollectionPodcastsContent(
                                    storeCollection = collection,
                                    navigateTo = navigateTo
                                )
                            }
                            is StoreCollectionEpisodes -> {
                                // header
                                StoreHeadingSectionWithLink(
                                    title = collection.label,
                                    onClick = { navigateTo(Screen.Room(collection.room)) }
                                )

                                // content
                                StoreCollectionEpisodesContent(
                                    storeCollection = collection,
                                    numRows = 3,
                                    navigateTo = navigateTo
                                )
                            }
                        }
                    }
                }
                .ifLoadingMore {
                    item {
                        Text(
                            modifier = Modifier.padding(
                                vertical = 16.dp,
                                horizontal = 4.dp
                            ),
                            text = "Loading next"
                        )
                    }
                }
        }

        ReachableAppBar(
            title = { Text(text = state.title) },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(Icons.Filled.ArrowBack,
                        contentDescription = null,)
                }
            },
            actions = {},
            state = listState,
            headerHeight = headerHeight)
    }
}