package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.theme.colors
import com.google.accompanist.coil.CoilImage


@Composable
fun PodcastListItem(
    modifier: Modifier = Modifier,
    storePodcast: StorePodcast,
    index: Int? = null,
    nameMaxLines: Int = 2,
    followingStatus: FollowStatus? = null,
    onSubscribeClick: (StorePodcast) -> Unit = { },
    iconModifier: Modifier = Modifier.size(PodcastDefaults.ThumbnailSize),
) {
    ListItem(
        modifier = modifier.fillMaxWidth(),
        text = {
            Text(with(AnnotatedString.Builder()) {
                if (index != null) {
                    pushStyle(SpanStyle(color = Color.Red))
                    append("${index}. ")
                    pop()
                }
                append(storePodcast.name)
                toAnnotatedString()
            },
                maxLines = nameMaxLines
            )
        },
        secondaryText = { Text(storePodcast.artistName) },
        icon = {
            PodcastThumbnail(
                data = storePodcast.getArtworkUrl(),
                modifier = iconModifier
            )
        },
        trailing = {
            FollowPodcastListIconButton(
                followingStatus = followingStatus,
                onSubscribeClick = { onSubscribeClick(storePodcast) }
            )
        }
    )
}

@Composable
fun SmallPodcastListItemIndexed(
    modifier: Modifier = Modifier,
    storePodcast: StorePodcast,
    index: Int,
) = PodcastListItem(
    modifier = modifier,
    storePodcast = storePodcast,
    index = index,
    nameMaxLines = 1,
    iconModifier = Modifier.size(PodcastDefaults.SmallThumbnailSize))


@Composable
fun PodcastThumbnail(
    modifier: Modifier = Modifier,
    data: Any,
    backgroundColor: Color = MaterialTheme.colors.surface,
) {
    Card(
        backgroundColor = colors[1],
        shape = RoundedCornerShape(8.dp)
    ) {
        CoilImage(
            data = data,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

@Composable
fun PodcastGridItem(
    modifier: Modifier = Modifier,
    podcast: StorePodcast,
    index: Int? = null,
    followingStatus: FollowStatus? = null,
    onSubscribeClick: (StorePodcast) -> Unit = { },
)
{
    Column(modifier = modifier) {
        Card(
            backgroundColor = colors[1],
            shape = RoundedCornerShape(8.dp)
        )
        {
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .aspectRatio(1f))
            {
                CoilImage(
                    data = podcast.getArtworkUrl(),
                    contentDescription = podcast.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth())

                FollowPodcastGridIconButton(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    followingStatus = followingStatus,
                    onSubscribeClick = { onSubscribeClick(podcast) }
                )
            }
        }
        Text(
            text = with(AnnotatedString.Builder()) {
                if (index != null) {
                    pushStyle(SpanStyle(color = Color.Red))
                    append("${index}. ")
                    pop()
                }
                append(podcast.name)
                toAnnotatedString()
            },
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.body2
        )
        Text(
            podcast.artistName,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption
        )
    }
}

@Composable
fun PodcastGridItem(
    modifier: Modifier = Modifier,
    podcast: Podcast,
    index: Int? = null,
)
{
    Column(modifier = modifier
        .padding(horizontal = 8.dp)) {
        Card(
            backgroundColor = colors[1],
            shape = RoundedCornerShape(8.dp)
        )
        {
            CoilImage(
                data = podcast.artworkUrl,
                contentDescription = podcast.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f))
        }
        Text(
            text = with(AnnotatedString.Builder()) {
                if (index != null) {
                    pushStyle(SpanStyle(color = Color.Red))
                    append("${index}. ")
                    pop()
                }
                append(podcast.name)
                toAnnotatedString()
            },
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.body2
        )
        Text(
            podcast.artistName,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption
        )
    }
}

@Composable
fun FollowPodcastGridIconButton(
    modifier: Modifier,
    followingStatus: FollowStatus? = null,
    onSubscribeClick: () -> Unit = { },
) {
    IconButton(
        modifier = modifier,
        onClick = { if (followingStatus == null) onSubscribeClick.invoke() })
    {
        Crossfade(
            modifier = Modifier,
            targetState = followingStatus,
            animationSpec = tween(500)) { followStatus ->
            when (followStatus) {
                FollowStatus.FOLLOWING -> {
                    Box(modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
                FollowStatus.FOLLOWED ->
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.action_following),
                        modifier = Modifier
                            .shadow(
                                elevation = 0.dp,
                                shape = MaterialTheme.shapes.small,
                                clip = true
                            )
                            .background(
                                color = MaterialTheme.colors.surface.copy(0.38f)
                            )
                    )
                else ->
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.action_follow),
                        modifier = Modifier
                            .shadow(
                                elevation = 2.dp,
                                shape = MaterialTheme.shapes.small,
                                clip = true
                            )
                            .background(
                                color = Color.White
                            )
                    )
            }
        }
    }
}

@Composable
fun FollowPodcastListIconButton(
    followingStatus: FollowStatus? = null,
    onSubscribeClick: () -> Unit = { },
) {
    Crossfade(
        targetState = followingStatus,
        animationSpec = tween(500)) { followStatus ->
        when (followStatus) {
            FollowStatus.FOLLOWING -> {
                Box(modifier = Modifier
                    .size(48.dp)) {
                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                }
            }
            FollowStatus.FOLLOWED ->
                IconButton(
                    onClick = { })
                {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.action_following),
                    )
                }
            else ->
                IconButton(
                    onClick = onSubscribeClick)
                {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.action_follow),
                    )
                }
        }
    }
}


object PodcastDefaults {
    /**
     * Default thumbnail size
     */
    val ThumbnailSize = 56.dp

    /**
     * Small thumbnail size
     */
    val SmallThumbnailSize = 40.dp
}