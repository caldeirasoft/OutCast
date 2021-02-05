package com.caldeirasoft.outcast.ui.screen.store.categories

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.ui.components.StoreHeadingSection
import com.caldeirasoft.outcast.ui.navigation.AmbientBottomDrawerState
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreGenreItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun CategoriesListScreen(
    selectedGenre: Int?,
    onGenreSelected: (Int?) -> Unit,
) {
    val scrollState = rememberScrollState(0f)
    val drawerState = AmbientBottomDrawerState.current
    Column()
    {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.store_tab_categories))
            },
            navigationIcon = {
                IconButton(onClick = { drawerState.close() }) {
                    Icon(imageVector = Icons.Default.Close)
                }
            },
            backgroundColor = Color . Transparent,
            elevation = if (scrollState.value > 0) 1.dp else 0.dp
        )

        ScrollableColumn(
            scrollState = scrollState,
            modifier = Modifier.padding(horizontal = 16.dp)) {
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
    val drawerState = AmbientBottomDrawerState.current
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
                    drawerState.close {
                        onGenreSelected(storeGenreItem.genreId)
                    }
                }),
            text = { Text(text = name, color = contentColor) },
            icon = {
                Image(imageVector = vectorResource(id = storeGenreItem.drawableId),
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
                    drawerState.close { onGenreSelected(null) }
                }),
            text = { Text(
                text = stringResource(id = R.string.store_genre_all),
                color = contentColor) },
        )
    }
}

class CategoriesListScreenArgs (
    val selectedGenre: Int?,
    val onGenreSelected: (Int?) -> Unit,
)
