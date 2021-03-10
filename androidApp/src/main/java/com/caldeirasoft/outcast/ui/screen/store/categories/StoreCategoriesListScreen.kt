package com.caldeirasoft.outcast.ui.screen.store.categories

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionGenres
import com.caldeirasoft.outcast.ui.components.GenreItem
import com.caldeirasoft.outcast.ui.components.ReachableAppBar
import com.caldeirasoft.outcast.ui.components.ReachableScaffold
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.util.px
import com.caldeirasoft.outcast.ui.util.toDp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreCategoriesScreen(
    storeCollection: StoreCollectionGenres,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    StoreCategoriesContent(
        storeCollection = storeCollection,
        navigateTo = navigateTo,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StoreCategoriesContent(
    storeCollection: StoreCollectionGenres,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val listState = rememberLazyListState(0)

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

            items(items = storeCollection.genres) { itemContent ->
                GenreItem(
                    genre = itemContent,
                    onGenreClick = { navigateTo(Screen.GenreScreen(itemContent)) })
            }
        }

        ReachableAppBar(
            title = { Text(text = storeCollection.label) },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(Icons.Filled.ArrowBack,
                        contentDescription = null,)
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Filled.Search,
                        contentDescription = null,)
                }
            },
            state = listState,
            headerHeight = headerHeight)
    }
}
