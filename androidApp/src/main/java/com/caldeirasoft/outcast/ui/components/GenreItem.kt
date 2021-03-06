package com.caldeirasoft.outcast.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.models.store.Genre
import com.caldeirasoft.outcast.ui.components.GenreDefaults.getIcon


@Composable
fun GenreItem(
    genre: Genre,
    onGenreClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable { onGenreClick() },
        text = { Text(text = genre.name) },
        icon = {
            Image(
                painter = painterResource(id = genre.getIcon()),
                contentDescription = genre.name,
                modifier = Modifier.size(32.dp)
            )
        }
    )
}

@Composable
fun GenreCardItem(
    genre: Genre,
    navigateToGenre: (Genre) -> Unit,
) {
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
        ListItem(
            modifier = Modifier,
            text = { Text(text = genre.name) },
            icon = {
                Image(
                    painter = painterResource(id = genre.getIcon()),
                    contentDescription = genre.name,
                    modifier = Modifier.size(32.dp)
                )
            }
        )
    }
}

@Composable
fun GenreGridItem(
    genre: Genre,
    onGenreClick: () -> Unit,
) = GenreDefaults.GridItem(genre, onGenreClick)


@Composable
fun GenreGridItemMore(
    genre: Genre,
    howManyMore: Int,
    onGenreClick: () -> Unit,
) = GenreDefaults.GridItemMore(genre, howManyMore, onGenreClick)


private object GenreDefaults {
    // content padding
    val ContentVerticalPadding = 8.dp

    @Composable
    fun GridItem(
        genre: Genre,
        onGenreClick: () -> Unit,
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onGenreClick)) {
            Column(Modifier
                .padding(
                    top = ContentVerticalPadding,
                    bottom = ContentVerticalPadding)
                .fillMaxSize())
            {
                Image(
                    painter = painterResource(id = genre.getIcon()),
                    contentDescription = genre.name,
                    modifier = Modifier
                        .size(24.dp)
                        .weight(2f)
                        .align(Alignment.CenterHorizontally)
                )

                Text(text = genre.name,
                    style = MaterialTheme.typography.caption.copy(fontSize = 11.sp),
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterHorizontally))
            }
        }
    }

    @Composable
    fun GridItemMore(
        genre: Genre,
        howManyMore: Int,
        onGenreClick: () -> Unit,
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onGenreClick)) {
            Column(Modifier
                .padding(top = ContentVerticalPadding, bottom = ContentVerticalPadding)
                .fillMaxSize())
            {
                Text(text = "+$howManyMore",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        letterSpacing = 0.15.sp
                    ),
                    modifier = Modifier
                        .height(24.dp)
                        .weight(2f)
                        .align(Alignment.CenterHorizontally))

                Text(text = genre.name,
                    style = MaterialTheme.typography.caption,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterHorizontally))
            }
        }
    }

    // genre icon
    @DrawableRes
    fun Genre.getIcon(): Int {
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