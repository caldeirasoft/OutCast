package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.ui.theme.colors
import com.skydoves.landscapist.coil.CoilImage


@Composable
fun StoreEpisodeListItem(episode: StoreEpisode)
{
    Row(modifier = Modifier
        .fillMaxWidth()
        //.clickable(onClick = { actions.navigateToStoreCollection(podcast.name, podcast.url) })
    ) {
        ListItem(
            text = { Text(text = episode.name) },
            secondaryText = { Text(text = episode.podcastName) },
            icon = {
                Card(
                    backgroundColor = colors[1],
                    shape = RoundedCornerShape(8.dp)
                ) {
                    CoilImage(
                        imageModel = episode.getArtworkUrl(),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .preferredSize(56.dp)
                    )
                }
            },
            trailing = {
                OutlinedButton(
                    colors = ButtonDefaults.textButtonColors(),
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(start = 4.dp,
                        end = 12.dp,
                        top = 0.dp,
                        bottom = 0.dp),
                    onClick = { },
                    modifier = Modifier.height(32.dp)
                ) {
                    Row {
                        Box(modifier = Modifier.preferredSize(24.dp).padding(end = 4.dp)) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = Color.Blue,
                                    radius = this.size.width / 2.0f)
                                drawArc(color = Color.Green,
                                    startAngle = 90f,
                                    sweepAngle = 90f,
                                    useCenter = true,
                                    size = Size(this.size.width, this.size.height))
                            }
                        }
                        Text(
                            modifier = Modifier.alignByBaseline(),
                            text = "text",
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                letterSpacing = 0.25.sp
                            )
                        )

                    }
                }

            }
        )
    }
}

@Composable
fun StoreEpisodeSmallListItemIndexed(
    episode: StoreEpisode,
    index: Int,
    navigateToEpisode: (StoreEpisode) -> Unit,
) {
    ListItem(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = { navigateToEpisode(episode) }),
        text = { Text(text = episode.name, maxLines = 1) },
        secondaryText = { Text(text = episode.podcastName) },
        icon = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically,
                modifier = Modifier.preferredWidth(60.dp)
            ) {
                Card(
                    backgroundColor = colors[1],
                    shape = RoundedCornerShape(8.dp)
                ) {
                    CoilImage(
                        imageModel = episode.getArtworkUrl(),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .preferredSize(40.dp)
                    )
                }
                Text(
                    index.toString(),
                    style = MaterialTheme.typography.body2
                )
            }
        }
    )


    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { navigateToEpisode(episode) }),
        text = { Text(text = episode.name, maxLines = 1) },
        secondaryText = { Text(text = episode.podcastName) },
        icon = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically,
                modifier = Modifier.preferredWidth(60.dp)
            ) {
                Card(
                    backgroundColor = colors[1],
                    shape = RoundedCornerShape(8.dp)
                ) {
                    CoilImage(
                        imageModel = episode.getArtworkUrl(),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .preferredSize(40.dp)
                    )
                }
                Text(
                    index.toString(),
                    style = MaterialTheme.typography.body2
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewPlayButton() {
    OutlinedButton(
        colors = ButtonDefaults.textButtonColors(),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(start = 4.dp,
            end = 12.dp,
            top = 0.dp,
            bottom = 0.dp),
        onClick = { },
        modifier = Modifier.height(32.dp)
    ) {
        Row {
            Box(modifier = Modifier.padding(end = 4.dp)) {
                Box(modifier = Modifier.preferredSize(24.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color.Blue,
                            radius = this.size.width / 2.0f)
                        drawArc(
                            color = Color.LightGray,
                            startAngle = -90f,
                            sweepAngle = 105f,
                            useCenter = true,
                            size = Size(this.size.width, this.size.height))
                        drawCircle(
                            color = Color.White,
                            radius = this.size.width / 2.0f * 0.75f)
                    }
                }
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "play",
                    tint = Color.Blue,
                    modifier = Modifier
                        .preferredSize(16.dp)
                        .align(Alignment.Center)
                )
            }
            Text(
                text = "text2",
                modifier = Modifier
                    .align(alignment = CenterVertically),
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    letterSpacing = 0.25.sp
                )
            )

        }
    }
}