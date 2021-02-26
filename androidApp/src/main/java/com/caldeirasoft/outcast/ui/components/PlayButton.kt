package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode


@Composable
fun PlayButton(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
) {
    OutlinedButton(
        colors = ButtonDefaults.textButtonColors(),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(
            start = 4.dp,
            end = 12.dp,
            top = 0.dp,
            bottom = 0.dp),
        onClick = { },
        modifier = Modifier.height(PlayButtonDefaults.Height)
    ) {
        Row {
            Box(modifier = Modifier.padding(end = 4.dp)) {
                Box(modifier = Modifier.size(24.dp)) {
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
                    contentDescription = null,
                    tint = Color.Blue,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center)
                )
            }
            Text(
                text = "text2",
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically),
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    letterSpacing = 0.25.sp
                )
            )
        }
    }
}

@Composable
fun QueueButton(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
) {
    IconButton(onClick = { /*TODO*/ }) {
        Icon(imageVector = Icons.Default.PlaylistAdd,
            contentDescription = null,)
    }
}

private object PlayButtonDefaults {
    // List item related defaults.
    val Height = 32.dp

    // Icon related defaults.
    val IconMinPaddedWidth = 40.dp
    val IconLeftPadding = 16.dp
    val IconThreeLineVerticalPadding = 16.dp

    // Default thumbnail size
    val ThumbnailSize = 40.dp

    // Content related defaults.
    val ContentLeftPadding = 16.dp
    val ContentRightPadding = 16.dp
    val ContentInnerPadding = 8.dp
    private val ThreeLineTrailingTopPadding = 16.dp
}
