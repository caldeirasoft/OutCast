package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.model.SeekbarFloatPreferenceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun SeekBarPreference(
    title: String,
    summary: String? = null,
    icon: ImageVector? = null,
    singleLineTitle: Boolean = false,
    enabled: Boolean = true,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    valueRepresentation: @Composable (Float) -> String,
    onValueChanged: (Float) -> Unit,
) {
    val currentValue = remember(value) { mutableStateOf(value) }
    Preference(
        title = title,
        singleLineTitle = singleLineTitle,
        enabled = enabled,
        icon = icon,
        summary = {
            SeekBarPreferenceSummary(
                summary = summary.orEmpty(),
                enabled = enabled,
                value = currentValue.value,
                valueRange = valueRange,
                steps = steps,
                valueRepresentation = valueRepresentation,
                onValueChanged = { currentValue.value = it },
                onValueChangeFinished = { onValueChanged(currentValue.value) }
            )
        },
    )
}

@Composable
private fun SeekBarPreferenceSummary(
    summary: String = "",
    enabled: Boolean = true,
    value: Float = 0F,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    valueRepresentation: @Composable (Float) -> String,
    onValueChanged: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Column {
        Text(text = summary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = valueRepresentation(value))
            Spacer(modifier = Modifier.width(16.dp))
            Slider(
                value = value,
                onValueChange = { if (enabled) onValueChanged(it) },
                valueRange = valueRange,
                steps = steps,
                onValueChangeFinished = onValueChangeFinished
            )
        }
    }
}