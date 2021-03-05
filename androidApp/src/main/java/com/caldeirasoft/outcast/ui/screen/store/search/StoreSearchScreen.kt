@file:OptIn(KoinApiExtension::class)
package com.caldeirasoft.outcast.ui.screen.store.search

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
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.px
import com.caldeirasoft.outcast.ui.util.toDp
import com.caldeirasoft.outcast.ui.util.toIntPx
import org.koin.core.component.KoinApiExtension
import kotlin.math.log10

@Composable
fun StoreSearchScreen(
    navigateTo: (Screen) -> Unit,
) {
    val viewModel: StoreSearchViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()
    val listState = rememberLazyListState(0)

    LaunchedEffect(state)  {
        if (state.storeGenreData is Uninitialized)
            viewModel.getGenres()
    }

    ReachableScaffold { headerHeight ->
        val spacerHeight = headerHeight - 36.px

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp))
        {
            item {
                Spacer(modifier = Modifier.height(spacerHeight.toDp()))
            }

            when (val storeGenre = state.storeGenreData)
            {
                is Loading ->
                    item {
                        ShimmerStoreCollectionsList()
                    }
                is Success -> {
                    val genreItems = storeGenre.invoke().genres
                    item {
                        // header
                        StoreHeadingSection(title = stringResource(id = R.string.store_tab_charts))
                    }
                    gridItems(
                        items = StoreItemType.values().toList(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalInnerPadding = 8.dp,
                        verticalInnerPadding = 8.dp,
                        columns = 2
                    ) { itemType ->
                        TopChartCardItem(
                            itemType = itemType,
                            navigateToTopChart = { navigateTo(Screen.Charts(itemType)) }
                        )
                    }
                    item {
                        // header
                        StoreHeadingSection(title = stringResource(id = R.string.store_tab_categories))
                    }
                    gridItems(
                        items = genreItems,
                        contentPadding = PaddingValues(16.dp),
                        horizontalInnerPadding = 8.dp,
                        verticalInnerPadding = 8.dp,
                        columns = 2
                    ) { genre ->
                        GenreCardItem(
                            genre = genre,
                            navigateToGenre = {
                                navigateTo(Screen.Genre(genre))
                            }
                        )
                    }
                }
                else -> {}
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
fun TopChartCardItem(
    itemType: StoreItemType,
    navigateToTopChart: (StoreItemType) -> Unit,
) {
    Card(
        border = ButtonDefaults.outlinedBorder,
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(24 / 9f)
            .clickable(onClick = {
                navigateToTopChart(itemType)
            })
    )
    {
        ListItem(
            modifier = Modifier,
            text = { Text(text = stringResource(id = when (itemType) {
                StoreItemType.PODCAST -> R.string.store_tab_chart_podcasts
                StoreItemType.EPISODE -> R.string.store_tab_chart_episodes
            })) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.TrendingUp,
                    contentDescription = itemType.name,
                    modifier = Modifier.size(32.dp)
                )
            }
        )
    }
}
