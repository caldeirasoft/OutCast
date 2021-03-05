@file:OptIn(KoinApiExtension::class)
package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionFeatured
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionItems
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionRooms
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf
import org.koin.core.component.KoinApiExtension

enum class StoreGenreItem(val genreId: Int, @StringRes val titleId: Int, @DrawableRes val drawableId: Int) {
    Arts(1301, R.string.store_genre_1301, R.drawable.ic_color_palette),
    Business(1321, R.string.store_genre_1321, R.drawable.ic_analytics),
    Comedy(1303, R.string.store_genre_1303, R.drawable.ic_theater),
    Education(1304, R.string.store_genre_1304, R.drawable.ic_mortarboard),
    Fiction(1483, R.string.store_genre_1483, R.drawable.ic_fiction),
    Government(1511, R.string.store_genre_1511, R.drawable.ic_city_hall),
    Health_Fitness(1512, R.string.store_genre_1512, R.drawable.ic_first_aid_kit),
    History(1487, R.string.store_genre_1487, R.drawable.ic_history),
    Kids_Family(1305, R.string.store_genre_1305, R.drawable.ic_family),
    Leisure(1502, R.string.store_genre_1502, R.drawable.ic_game_controller),
    Music(1310, R.string.store_genre_1310, R.drawable.ic_guitar),
    News(1489, R.string.store_genre_1489, R.drawable.ic_news),
    Religion_Spirtuality(1314, R.string.store_genre_1314, R.drawable.ic_religion),
    Science(1533, R.string.store_genre_1533, R.drawable.ic_flasks),
    Society_Culture(1324, R.string.store_genre_1324, R.drawable.ic_social_care),
    Sports(1545, R.string.store_genre_1545, R.drawable.ic_sport),
    TV_Film(1309, R.string.store_genre_1309, R.drawable.ic_video_camera),
    Technology(1318, R.string.store_genre_1318, R.drawable.ic_artificial_intelligence),
    True_Crime(1488, R.string.store_genre_1488, R.drawable.ic_handcuffs)
}

@ExperimentalAnimationApi
@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreDirectoryScreen(
    navigateTo: (Screen) -> Unit,
) {
    val viewModel : StoreDirectoryViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()

    LaunchedEffect(state)  {
        if (state.storeFront == null)
            viewModel.getDiscover()
    }
    StoreDirectoryContent(
        state = state,
        navigateTo = navigateTo
    )
}

@ExperimentalAnimationApi
@Composable
private fun StoreDirectoryContent(
    state: StoreDirectoryViewState,
    navigateTo: (Screen) -> Unit,
) {
    val listState = rememberLazyListState(0)
    val lazyPagingItems = flowOf(state.discover).collectAsLazyPagingItems()

    ReachableScaffold { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp))
        {
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
                            is StoreCollectionItems -> {
                                // header
                                StoreHeadingSectionWithLink(
                                    title = collection.label,
                                    onClick = { navigateTo(Screen.Room(collection.room)) }
                                )
                                // content
                                StoreCollectionItemsContent(
                                    storeCollection = collection,
                                    navigateTo = navigateTo
                                )
                            }
                            is StoreCollectionRooms -> {
                                // header
                                StoreHeadingSection(title = collection.label)
                                // genres
                                StoreCollectionRoomsContent(
                                    storeCollection = collection,
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
            title = {
                Text(text = stringResource(id = R.string.store_tab_discover))
            },
            actions = {

            },
            state = listState,
            headerHeight = headerHeight)
    }
}
