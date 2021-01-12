package com.caldeirasoft.outcast.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.animation.transition
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.HorizontalGradient
import androidx.compose.ui.graphics.VerticalGradient
import androidx.compose.ui.unit.dp

import com.caldeirasoft.outcast.ui.theme.AnimationDefinitions
import com.caldeirasoft.outcast.ui.theme.colors

@Composable
fun LoadingListShimmer() {
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

    Column {
        ShimmerCollectionPodcastContent(list = list, floatAnim = dpValue.value)
        ShimmerCollectionPodcastContent(list = list, floatAnim = dpValue.value)
        ShimmerCollectionPodcastContent(list = list, floatAnim = dpValue.value)
        ShimmerCollectionPodcastContent(list = list, floatAnim = dpValue.value)
        ShimmerCollectionPodcastContent(list = list, floatAnim = dpValue.value)
    }
}

@Composable
fun ShimmerCollectionPodcastContent(
    list: List<Color>,
    floatAnim: Float = 0f
) {
    val brush = VerticalGradient(list, 0f, floatAnim)
    Column(modifier = Modifier
        .padding(8.dp)) {
        Surface {
            Spacer(modifier = Modifier
                .preferredSize(width = 250.dp, height = 30.dp)
                .padding(horizontal = 8.dp)
                .background(brush = brush))
        }
        Spacer(modifier = Modifier.height(8.dp)
            .padding(vertical = 8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth())
        {
            for (i in 1..10) {
                ShimmerPodcastGridItem(list = list, floatAnim = floatAnim)
            }
        }
        Spacer(modifier = Modifier.height(8.dp)
            .padding(vertical = 8.dp))
    }
}

@Composable
fun ShimmerPodcastGridItem(
    list: List<Color>,
    floatAnim: Float = 0f
) {
    val brush = VerticalGradient(list, 0f, floatAnim)
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