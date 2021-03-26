package com.caldeirasoft.outcast.ui.components.foundation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.ui.util.applyTextStyleCustom
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T : Any> FilterChipGroup(
    modifier: Modifier = Modifier,
    selectedValue: T?,
    values: Array<T>,
    onClick: (T?) -> Unit,
    text: @Composable (T) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier.horizontalScroll(state = scrollState)
    ) {
        Surface(
            //color = MaterialTheme.colors.onSurface.copy(alpha = 0.48f),
            contentColor = MaterialTheme.colors.onSurface,
            border = BorderStroke(width = 1.dp,
                selectedValue
                    ?.let { MaterialTheme.colors.primary }
                    ?: MaterialTheme.colors.onSurface.copy(alpha = 0.48f)
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row {
                values
                    .forEachIndexed { index, value ->
                        AnimatedVisibility(
                            visible = selectedValue == null || selectedValue == value,
                            enter = expandHorizontally(animationSpec = tween(durationMillis = 250))
                                    + fadeIn(animationSpec = tween(durationMillis = 250)),
                            exit = shrinkHorizontally(animationSpec = tween(durationMillis = 250))
                                    + fadeOut(animationSpec = tween(durationMillis = 250)),
                        ) {
                            ChipContent(
                                selected = selectedValue == value,
                                onClick = {
                                    if (selectedValue != value)
                                        onClick(value)
                                    else onClick(null)

                                    coroutineScope.launch {
                                        scrollState.animateScrollTo(0)
                                    }
                                }
                            ) {
                                val styledText =
                                    applyTextStyleCustom(MaterialTheme.typography.body2,
                                        ContentAlpha.high) {
                                        text(value)
                                    }
                                styledText()
                            }
                        }
                    }
            }
        }
    }
}

@Composable
private fun ChipContent(
    selected: Boolean,
    onClick: () -> Unit,
    text: @Composable () -> Unit,
) {
    val backgroundColor: Color = when {
        selected -> MaterialTheme.colors.primary
        else -> Color.Transparent
    }
    TextButton(
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = animateColorAsState(backgroundColor).value,
            contentColor = animateColorAsState(contentColorFor(backgroundColor)).value,
            disabledContentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.disabled)
        ),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(0.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .wrapContentHeight()) {
            text()
        }
    }
}
