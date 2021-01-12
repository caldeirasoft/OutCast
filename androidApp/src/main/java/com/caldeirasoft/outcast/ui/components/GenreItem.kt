package com.caldeirasoft.outcast.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import com.caldeirasoft.outcast.ui.components.GenreDefaults.getIcon


@Composable
fun GenreItem(
    storeGenre: StoreGenre,
    onGenreClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onGenreClick),
        text = { Text(text = storeGenre.name) },
        icon = {
            Image(imageVector = vectorResource (id = storeGenre.getIcon()),
                modifier = Modifier.size(32.dp)
            )
        }
    )
}

@Composable
fun GenreGridItem(
    storeGenre: StoreGenre,
    onGenreClick: () -> Unit,
) = GenreDefaults.GridItem(storeGenre, onGenreClick)


@Composable
fun GenreGridItemMore(
    storeGenre: StoreGenre,
    howManyMore: Int,
    onGenreClick: () -> Unit,
) = GenreDefaults.GridItemMore(storeGenre, howManyMore, onGenreClick)


private object GenreDefaults {
    // content padding
    val ContentHorizontalPadding = 16.dp
    // content padding
    val ContentHorizontalInnerPadding = 24.dp
    // min height
    val MinHeight = 64.dp

    @Composable
    fun GridItem(
        storeGenre: StoreGenre,
        onGenreClick: () -> Unit,
    ) {
        Box(modifier = Modifier.fillMaxWidth().preferredHeight(MinHeight)) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                border = ButtonDefaults.outlinedBorder,
                elevation = 0.dp,
                modifier = Modifier.fillMaxSize().clickable(onClick = onGenreClick)
            ) {
                Row(Modifier
                    .padding(start = ContentHorizontalPadding, end = ContentHorizontalPadding)
                    .fillMaxSize()) {
                    val columnModifier = Modifier.weight(1f)
                        .padding(start = ContentHorizontalPadding, end = ContentHorizontalPadding)

                    Image(imageVector = vectorResource(id = storeGenre.getIcon()),
                        modifier = Modifier.size(32.dp).align(Alignment.CenterVertically)
                    )

                    Text(text = storeGenre.name,
                        modifier = Modifier
                            .padding(start = ContentHorizontalInnerPadding)
                            .align(Alignment.CenterVertically))
                }
            }
        }
    }

    @Composable
    fun GridItemMore(
        storeGenre: StoreGenre,
        howManyMore: Int,
        onGenreClick: () -> Unit,
    ) {
        Box(modifier = Modifier.fillMaxWidth().preferredHeight(MinHeight)) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                border = ButtonDefaults.outlinedBorder,
                elevation = 0.dp,
                modifier = Modifier.fillMaxSize().clickable(onClick = onGenreClick)
            ) {
                Row(Modifier
                    .padding(start = ContentHorizontalPadding, end = ContentHorizontalPadding)
                    .fillMaxSize()) {
                    Text(text = "+$howManyMore",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            letterSpacing = 0.15.sp
                        ),
                        modifier = Modifier.width(32.dp).align(Alignment.CenterVertically))

                    Text(text = storeGenre.name,
                        modifier = Modifier
                            .padding(start = ContentHorizontalInnerPadding)
                            .align(Alignment.CenterVertically))
                }
            }
        }
    }

    // genre icon
    @DrawableRes
    fun StoreGenre.getIcon(): Int {
        return when(this.id) {
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
    }
}