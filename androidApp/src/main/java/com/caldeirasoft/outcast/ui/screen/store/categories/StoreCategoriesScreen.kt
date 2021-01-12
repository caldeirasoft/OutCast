package com.caldeirasoft.outcast.ui.screen.store

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ambientOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionGenres
import com.caldeirasoft.outcast.ui.components.GenreItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreCategoriesScreen(
    storeCollection: StoreCollectionGenres,
    navigateToGenre: (Int, String) -> Unit,
    navigateUp: () -> Unit,
) {
    StoreCategoriesContent(
        storeCollection = storeCollection,
        navigateToGenre = navigateToGenre,
        navigateUp = navigateUp,
    )
}

@Composable
private fun StoreCategoriesContent(
    storeCollection: StoreCollectionGenres,
    navigateToGenre: (Int, String) -> Unit,
    navigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(storeCollection.label) },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    )
    {
        LazyColumn {
            items(storeCollection.genres) { itemContent ->
                GenreItem(
                    storeGenre = itemContent,
                    onGenreClick = { navigateToGenre(itemContent.id, itemContent.name) })
            }
        }
    }
}
