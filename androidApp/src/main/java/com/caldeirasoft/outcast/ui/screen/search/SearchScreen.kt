package com.caldeirasoft.outcast.ui.screen.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import com.caldeirasoft.outcast.ui.components.ScaffoldWithLargeHeaderAndLazyColumn
import com.caldeirasoft.outcast.ui.components.StoreHeadingSection
import com.caldeirasoft.outcast.ui.components.gridItems
import com.caldeirasoft.outcast.ui.screen.base.Screen
import com.caldeirasoft.outcast.ui.screen.store.categories.drawableId
import com.caldeirasoft.outcast.ui.screen.store.storedata.RoutesActions
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.ColorHash
import com.caldeirasoft.outcast.ui.util.navigateToStore
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import cz.levinzonr.router.core.Route


@OptIn(ExperimentalFoundationApi::class)
@Route(name = "search")
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    navController: NavController,
) {
    Screen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is SearchViewModel.Event.OpenStoreData ->
                    navController.navigateToStore(event.storeData)
                is SearchViewModel.Event.OpenSearchResults ->
                    navController.navigate(RoutesActions.toSearch_results())
                is SearchViewModel.Event.Exit ->
                    navController.navigateUp()
            }
        }
    ) { state, performAction ->
        SearchScreen(
            state = state,
            performAction = performAction
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun SearchScreen(
    state: SearchViewModel.State,
    performAction: (SearchViewModel.Action) -> Unit
) {
    ScaffoldWithLargeHeaderAndLazyColumn(
        title = stringResource(id = R.string.screen_search),
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        showTopBar = false,
    ) {
        // search bar
        stickyHeader {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { performAction(SearchViewModel.Action.OpenSearchResults) }
            )
        }

        val genreItems =
            state.root
                ?.let { StoreGenre(-1, "", "", "")}
                ?.let { listOf(it) }
                ?.plus(state.genres)
                ?: listOf()

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
            val index = genreItems.indexOf(genre)
            val color = getBackgroundColor(index)
            when (genre.id) {
                -1 -> TopChartCardItem(
                    backgroundColor = color,
                    onClick = {
                        performAction(SearchViewModel.Action.OpenTopCharts)
                    }
                )
                else -> {
                    CategoryCardItem(
                        backgroundColor = color,
                        genre = genre,
                        navigateToGenre = {
                            performAction(SearchViewModel.Action.OpenStoreCategory(it))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
)
{
    // search button
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.medium),
            disabledContentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.disabled)
        ),
        shape = RoundedCornerShape(50)
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
    backgroundColor: Color,
    onClick: () -> Unit,
) {
    val title = stringResource(id = R.string.store_tab_chart_podcasts)
    val contentColor =
        if (backgroundColor.luminance() > 0.5)
            Color.Black else Color.White
    Card(
        border = ButtonDefaults.outlinedBorder,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = 0.dp,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(24 / 12f)
    )
    {
        ListItem(
            modifier = Modifier,
            text = {
                Text(text = title, style = typography.h6.copy(fontSize = 16.sp))
            },
        )
    }
}

@Composable
fun CategoryCardItem(
    category: Category,
    navigateToCategory: (Category) -> Unit,
) {
    Card(
        border = ButtonDefaults.outlinedBorder,
        shape = RoundedCornerShape(8.dp),
        elevation = 0.dp,
        onClick = {
            navigateToCategory(category)
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(24 / 9f)
    )
    {
        ListItem(
            modifier = Modifier,
            text = { Text(text = category.text) },
        )
    }
}

@Composable
fun CategoryCardItem(
    genre: StoreGenre,
    backgroundColor: Color,
    navigateToGenre: (StoreGenre) -> Unit,
) {
    val contentColor =
        if (backgroundColor.luminance() > 0.5)
            Color.Black else Color.White
    Card(
        border = ButtonDefaults.outlinedBorder,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = 0.dp,
        onClick = { navigateToGenre(genre) },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(24 / 12f)
    )
    {
        ListItem(
            modifier = Modifier,
            text = { Text(text = genre.name, style = typography.h6.copy(fontSize = 16.sp)) },
        )
    }
}

fun getBackgroundColor(index: Int): Color {
    val red700 = Color(0xFFD32F2F)
    val red900 = Color(0xFFB71C1C)
    val redA700 = Color(0xFFD50000)
    val pink700 = Color(0xFFC2185B)
    val pinkA700 = Color(0xFFC51162)
    val purple700 = Color(0xFF7B1FA2)
    val deepPurple900 = Color(0xFF311B92)
    val deepPurpleA700 = Color(0xFF6200EA)
    val indigo500 = Color(0xFF3F51B5)
    val indigo800 = Color(0xFF283593)
    val blue800 = Color(0xFF1565C0)
    val lightBlue800 = Color(0xFF0277BD)
    val lightBlue900 = Color(0xFF01579B)
    val teal700 = Color(0xFF00796B)
    val teal900 = Color(0xFF004D40)
    val green900 = Color(0xFF2E7D32)
    val orange900 = Color(0xFFE65100)
    val brown500 = Color(0xFF795548)
    val brown900 = Color(0xFF3E2723)
    val gray700 = Color(0xFF616161)
    val deepOrange900 = Color(0xFFBF360C)
    val deepOrangeA700 = Color(0xFFDD2C00)
    val colorList = listOf(
        red700,
        deepOrangeA700,
        gray700,
        teal900,
        indigo500,
        pinkA700,
        blue800,
        brown900,
        green900,
        deepOrange900,
        deepPurple900,
        lightBlue900,
        teal700,
        brown500,
        indigo800,
        purple700,
        redA700,
        pink700,
        lightBlue800,
        orange900,
        deepPurpleA700
    )

    return colorList[index % colorList.size]
}
