package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerStoreCollectionsList() =
    LoadingListShimmer { list, floatAnim ->
        Column {
            repeat(6) {
                ShimmerCollectionPodcastContent(list = list, floatAnim = floatAnim)
            }
        }
    }

@Composable
fun ShimmerStorePodcastList() =
    LoadingListShimmer { list, floatAnim ->
        Column {
            repeat(6) {
                ShimmerPodcastListItem(list = list, floatAnim = floatAnim)
                Divider()
            }
        }
    }


@Composable
fun ShimmerCollectionPodcastContent(
    list: List<Color>,
    floatAnim: Float = 0f
) {
    val brush = Brush.verticalGradient(list, 0f, floatAnim)
    Column(modifier = Modifier
        .padding(8.dp)) {
        Surface {
            Spacer(modifier = Modifier
                .size(width = 250.dp, height = 30.dp)
                .padding(horizontal = 8.dp)
                .background(brush = brush))
        }
        Spacer(modifier = Modifier
            .height(8.dp)
            .padding(vertical = 8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth())
        {
            repeat(10) {
                ShimmerPodcastGridItem(list = list, floatAnim = floatAnim)
            }
        }
        Spacer(modifier = Modifier
            .height(8.dp)
            .padding(vertical = 8.dp))
    }
}

@Composable
fun ShimmerPodcastGridItem(
    list: List<Color>,
    floatAnim: Float = 0f
) {
    val brush = Brush.verticalGradient(list, 0f, floatAnim)
    Column(modifier = Modifier
        .padding(8.dp)
        .width(100.dp)
        ) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Spacer(modifier = Modifier
                .size(100.dp)
                .background(brush = brush))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)
                .background(brush = brush))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(brush = brush))
        }
    }
}

@Composable
fun ShimmerPodcastListItem(
    list: List<Color>,
    floatAnim: Float = 0f
) {
    val brush = Brush.verticalGradient(list, 0f, floatAnim)
    Row(modifier = Modifier
        .padding(8.dp)
    ) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Spacer(modifier = Modifier
                .size(56.dp)
                .background(brush = brush))
        }
        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
                .background(brush = brush))
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)
                .background(brush = brush))
        }
    }
}

@Composable
private fun LoadingListShimmer(innerContent: @Composable (List<Color>, Float) -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 100.dp.value,
        targetValue = 2000.dp.value,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ))

    val shimmerColor: Color by infiniteTransition.animateColor(
        initialValue = Color.LightGray.copy(alpha = 0.6f),
        targetValue = Color.LightGray,
        animationSpec = infiniteRepeatable(
            animation = KeyframesSpec(
                KeyframesSpec.KeyframesSpecConfig<Color>().apply {
                    Color.LightGray.copy(alpha = 0.6f) at 0 //ms
                    Color.LightGray.copy(alpha = 0.9f) at 200 //ms
                    Color.LightGray at 400 //ms
                    Color.LightGray.copy(alpha = 0.6f) at 600 //ms
                }
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    val list = listOf(
        shimmerColor,
        shimmerColor.copy(alpha = 0.5f)
    )
    val dpValue = shimmerTranslate.dp

    innerContent(list, dpValue.value)
}

