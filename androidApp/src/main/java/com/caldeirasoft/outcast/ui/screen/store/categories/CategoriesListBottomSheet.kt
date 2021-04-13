package com.caldeirasoft.outcast.ui.screen.store.categories

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

enum class StoreGenreItem(
    val genreId: Int,
    @StringRes val titleId: Int,
    @DrawableRes val drawableId: Int,
) {
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

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun CategoriesListBottomSheet(
    selectedGenre: Int?,
    onGenreSelected: (Int?) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState(0)
    val drawerState = LocalBottomSheetState.current
    Column()
    {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.store_tab_categories))
            },
            navigationIcon = {
                IconButton(onClick = {
                    coroutineScope.launch {
                        drawerState.hide()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            },
            backgroundColor = Color . Transparent,
            elevation = if (scrollState.value > 0) 1.dp else 0.dp
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)) {
            Surface(
                border = ButtonDefaults.outlinedBorder,
                shape = RoundedCornerShape(8.dp)) {
                Column {
                    // all
                    GenreListItem(null, selectedGenre == null, onGenreSelected)
                    Divider()

                    StoreGenreItem.values().toList().forEach { itemContent ->
                        GenreListItem(itemContent,
                            selectedGenre == itemContent.genreId,
                            onGenreSelected)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun GenreListItem(
    storeGenreItem: StoreGenreItem?,
    selected: Boolean = false,
    onGenreSelected: (Int?) -> Unit)
{
    val coroutineScope = rememberCoroutineScope()
    val drawerState = LocalBottomSheetState.current
    val backgroundColor: Color = when {
        selected -> MaterialTheme.colors.primary.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    val contentColor: Color = when {
        selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }

    if (storeGenreItem != null) {
        val name = stringResource(id = storeGenreItem.titleId)
        ListItem(
            modifier = Modifier
                .background(backgroundColor)
                .clickable(onClick = {
                    onGenreSelected(storeGenreItem.genreId)
                    coroutineScope.launch {
                        drawerState.hide()
                    }
                }),
            text = { Text(text = name, color = contentColor) },
            icon = {
                Image(painter = painterResource(id = storeGenreItem.drawableId),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        )
    }
    else {
        ListItem(
            modifier = Modifier
                .background(backgroundColor)
                .clickable(onClick = {
                    onGenreSelected(null)
                    coroutineScope.launch {
                        drawerState.hide()
                    }
                }),
            text = { Text(
                text = stringResource(id = R.string.store_genre_all),
                color = contentColor) },
        )
    }
}
