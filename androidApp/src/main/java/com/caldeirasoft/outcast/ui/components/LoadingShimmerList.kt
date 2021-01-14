package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.transition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.VerticalGradient
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.ui.theme.AnimationDefinitions

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
                .preferredSize(width = 250.dp, height = 30.dp)
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
        .preferredWidth(100.dp)
        ) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Spacer(modifier = Modifier
                .preferredSize(100.dp)
                .background(brush = brush))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .preferredHeight(15.dp)
                .background(brush = brush))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .preferredHeight(12.dp)
                .background(brush = brush))
        }
    }
}

@Composable
fun ShimmerPodcastListItem(
    list: List<Color>,
    floatAnim: Float = 0f
) {
    val brush = VerticalGradient(list, 0f, floatAnim)
    Row(modifier = Modifier
        .padding(8.dp)
    ) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Spacer(modifier = Modifier
                .preferredSize(56.dp)
                .background(brush = brush))
        }
        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .preferredHeight(25.dp)
                .background(brush = brush))
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .preferredHeight(15.dp)
                .background(brush = brush))
        }
    }
}

@Composable
private fun LoadingListShimmer(innerContent: @Composable (List<Color>, Float) -> Unit) {
    val dpStartState by remember { mutableStateOf(AnimationDefinitions.AnimationState.START) }
    val dpEndState by remember { mutableStateOf(AnimationDefinitions.AnimationState.END) }

    val shimmerTranslateAnim = transition(
        definition = AnimationDefinitions.shimmerTranslateAnimation,
        initState = dpStartState,
        toState = dpEndState
    )

    val shimmerColorAnim = transition(
        definition = AnimationDefinitions.shimmerColorAnimation,
        initState = AnimationDefinitions.AnimationState.START,
        toState = AnimationDefinitions.AnimationState.END
    )

    val list = listOf(
        shimmerColorAnim[AnimationDefinitions.shimmerColorPropKey],
        shimmerColorAnim[AnimationDefinitions.shimmerColorPropKey].copy(alpha = 0.5f)
    )
    val dpValue = shimmerTranslateAnim[AnimationDefinitions.shimmerDpPropKey]

    innerContent(list, dpValue.value)
}

