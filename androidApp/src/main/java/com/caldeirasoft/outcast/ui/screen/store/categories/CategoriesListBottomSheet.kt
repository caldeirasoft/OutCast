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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.common.Constants.Companion.DEFAULT_GENRE
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@Composable
@OptIn(FlowPreview::class)
fun CategoriesListBottomSheet(
    categories: List<StoreCategory>,
    selectedCategory: StoreCategory,
    navigateUp: () -> Unit,
    onCategorySelected: (StoreCategory) -> Unit
) {
    val scrollState = rememberScrollState(0)
    Column()
    {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.store_tab_categories))
            },
            navigationIcon = {
                IconButton(onClick = navigateUp) {
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
                    categories
                        .forEach { itemContent ->
                            CategoryListItem(
                                category = itemContent,
                                selected = selectedCategory == itemContent,
                                onCategorySelected = {
                                    onCategorySelected(it)
                                    navigateUp()
                                }
                            )
                            Divider()
                        }
                }
            }
        }
    }
}

@Composable
fun CategoryListItem(
    category: StoreCategory,
    selected: Boolean = false,
    onCategorySelected: (StoreCategory) -> Unit)
{
    val backgroundColor: Color = when {
        selected -> MaterialTheme.colors.primary.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    val contentColor: Color = when {
        selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }

    val name = category.name
    if (category.id != DEFAULT_GENRE) {
        ListItem(
            modifier = Modifier
                .background(backgroundColor)
                .clickable(onClick = {
                    onCategorySelected(category)
                }),
            text = { Text(text = name, color = contentColor) },
            icon = {
                category.getDrawableId()?.let { drawableId ->
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        )
    }
    else {
        ListItem(
            modifier = Modifier
                .background(backgroundColor)
                .clickable(onClick = {
                    onCategorySelected(category)
                }),
            text = { Text(text = name, color = contentColor) },
        )
    }
}

// genre icon

val Category.drawableId: Int
    @DrawableRes
    get() = when(this.id) {
        1301 -> R.drawable.ic_color_palette
        1321 -> R.drawable.ic_analytics
        1303 -> R.drawable.ic_theater
        1304 -> R.drawable.ic_mortarboard
        1483 -> R.drawable.ic_fiction
        1511 -> R.drawable.ic_city_hall
        1512 -> R.drawable.ic_first_aid_kit
        1487 -> R.drawable.ic_history
        1305 -> R.drawable.ic_family
        1502 -> R.drawable.ic_game_controller
        1310 -> R.drawable.ic_guitar
        1489 -> R.drawable.ic_news
        1314 -> R.drawable.ic_religion
        1533 -> R.drawable.ic_flasks
        1324 -> R.drawable.ic_social_care
        1545 -> R.drawable.ic_sport
        1309 -> R.drawable.ic_video_camera
        1318 -> R.drawable.ic_artificial_intelligence
        1488 -> R.drawable.ic_handcuffs
        else -> R.drawable.ic_analytics
    }

@DrawableRes
fun StoreCategory.getDrawableId() : Int? = when(this.id) {
        1301 -> R.drawable.ic_color_palette
        1321 -> R.drawable.ic_analytics
        1303 -> R.drawable.ic_theater
        1304 -> R.drawable.ic_mortarboard
        1483 -> R.drawable.ic_fiction
        1511 -> R.drawable.ic_city_hall
        1512 -> R.drawable.ic_first_aid_kit
        1487 -> R.drawable.ic_history
        1305 -> R.drawable.ic_family
        1502 -> R.drawable.ic_game_controller
        1310 -> R.drawable.ic_guitar
        1489 -> R.drawable.ic_news
        1314 -> R.drawable.ic_religion
        1533 -> R.drawable.ic_flasks
        1324 -> R.drawable.ic_social_care
        1545 -> R.drawable.ic_sport
        1309 -> R.drawable.ic_video_camera
        1318 -> R.drawable.ic_artificial_intelligence
        1488 -> R.drawable.ic_handcuffs
        else -> null
    }
