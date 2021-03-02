@file:OptIn(KoinApiExtension::class)
package com.caldeirasoft.outcast.ui.screen.store.search

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import com.caldeirasoft.outcast.ui.components.GenreItem
import com.caldeirasoft.outcast.ui.components.ReachableScaffold
import com.caldeirasoft.outcast.ui.components.gridItems
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.getViewModel
import com.caldeirasoft.outcast.ui.util.px
import com.caldeirasoft.outcast.ui.util.toDp
import com.caldeirasoft.outcast.ui.util.toIntPx
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.core.component.KoinApiExtension
import kotlin.math.log10

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreSearchScreen(
    navigateTo: (Screen) -> Unit,
) {
    val viewModel: StoreSearchViewModel = getViewModel()
    val viewState by viewModel.state.collectAsState()

    StoreSearchContent(
        viewState = viewState,
        navigateTo = navigateTo,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StoreSearchContent(
    viewState: StoreSearchViewModel.State,
    navigateTo: (Screen) -> Unit,
) {
    val listState = rememberLazyListState(0)
    val genreItems = viewState.storeData?.genres?.genres ?: emptyList()

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

            gridItems(
                items = genreItems,
                contentPadding = PaddingValues(16.dp),
                horizontalInnerPadding = 8.dp,
                verticalInnerPadding = 8.dp,
                columns = 2
            ) { storeGenre ->
                CardGenreItem(
                    genre = storeGenre,
                    navigateToGenre = { navigateTo(Screen.Genre(storeGenre.id, storeGenre.name)) }
                )
            }
        }


        ReachableAppBarWithSearchBar(
            title = {
                Text(text = stringResource(id = R.string.store_tab_discover))
            },
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            },
            state = listState,
            headerHeight = headerHeight)
    }
}

@ExperimentalAnimationApi
@Composable
private fun ReachableAppBarWithSearchBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    state: LazyListState,
    headerHeight: Int)
{
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(headerHeight.toDp()))
    {
        val scrollRatioLargeHeader =
            if (headerHeight != 0)
                ((headerHeight - (state.firstVisibleItemIndex * headerHeight.toFloat() + state.firstVisibleItemScrollOffset)) / headerHeight)
                    .coerceAtLeast(0f)
            else 1f
        val minimumHeight = 56.dp
        val computedHeight = (scrollRatioLargeHeader * headerHeight).toDp().coerceAtLeast(minimumHeight)
        val alphaLargeHeader = (3 * log10(scrollRatioLargeHeader.toDouble()) + 1).toFloat().coerceIn(0f, 1f)
        val alphaCollapsedHeader = (3 * log10((1-scrollRatioLargeHeader).toDouble()) + 1).toFloat().coerceIn(0f, 1f)
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = computedHeight)
            .height(computedHeight)
            .background(MaterialTheme.colors.background)) {

            // large title
            Box(modifier = Modifier
                .padding(bottom = (56.dp.toIntPx() * scrollRatioLargeHeader).toDp())
                .align(Alignment.Center)
                .alpha(alphaLargeHeader)) {
                ProvideTextStyle(typography.h4, title)
            }

            // top app bar
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .alpha(alphaCollapsedHeader),
                title = {
                    CompositionLocalProvider(LocalContentAlpha provides alphaCollapsedHeader) {
                        title()
                    }
                },
                navigationIcon = navigationIcon,
                actions = actions,
                backgroundColor = Color.Transparent,
                elevation = if (state.firstVisibleItemIndex > 0) 1.dp else 0.dp
            )

            // search bar
            SearchBar(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .alpha(alphaLargeHeader))
        }

    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier)
{
    // search button
    OutlinedButton(
        onClick = {},
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
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
                contentDescription = null,
            )
            Text("Search", modifier = Modifier.padding(horizontal = 4.dp))
        }
    }
}


@Composable
private fun CardGenreItem(
    genre: StoreGenre,
    navigateToGenre: (StoreGenre) -> Unit,
) {
    val primaryColor = colors[1]
    val dominantColor = remember(genre) { mutableStateOf(primaryColor) }
    /*FetchDominantColorFromPoster(
        posterUrl = requireNotNull(genre.artwork?.url),
        colorState = dominantColor)*/
    Card(
        border = ButtonDefaults.outlinedBorder,
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(24 / 9f)
            .clickable(onClick = {
                navigateToGenre(genre)
            })
    )
    {
        GenreItem(storeGenre = genre, onGenreClick = {  })
    }
}
