package com.caldeirasoft.outcast.ui.theme

import androidx.compose.animation.ColorPropKey
import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object AnimationDefinitions {
    enum class AnimationState {
        START,
        MID,
        END
    }

    val shimmerColorPropKey = ColorPropKey(label = "shimmerColor")
    val shimmerColorAnimation = transitionDefinition<AnimationState> {
        state(AnimationState.START) { this[shimmerColorPropKey] = Color.LightGray.copy(alpha = 0.6f) }
        state(AnimationState.MID) { this[shimmerColorPropKey] = Color.LightGray.copy(alpha = 0.9f) }
        state(AnimationState.END) { this[shimmerColorPropKey] = Color.LightGray }
        transition(
            AnimationState.START to AnimationState.MID,
            AnimationState.MID to AnimationState.END,
            AnimationState.END to AnimationState.START
        ) {
            shimmerColorPropKey using tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            )
        }
    }

    val shimmerDpPropKey = DpPropKey(label = "shimmerdp")
    val shimmerTranslateAnimation = transitionDefinition<AnimationState> {
        state(AnimationState.START) { this[shimmerDpPropKey] = 100.dp }
        state(AnimationState.END) { this[shimmerDpPropKey] = 2000.dp }
        transition(AnimationState.START, AnimationState.END)
        {
            shimmerDpPropKey using InfiniteRepeatableSpec(
                animation = tween(
                    durationMillis = 1200,
                    easing = LinearEasing
                )
            )
        }
    }
}