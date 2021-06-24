package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.model.NumberRangeFloatPreferenceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun NumberRangePreference(
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
        },
        trailing = {
            PreferenceTrailing(
                value = currentValue.value,
                valueRange = valueRange,
                steps = steps,
                valueRepresentation = valueRepresentation,
                onValueChanged = {
                    currentValue.value = it
                    onValueChanged(currentValue.value)
                },
            )
        }
    )
}

@Composable
private fun PreferenceTrailing(
    value: Float = 0F,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    valueRepresentation: @Composable (Float) -> String,
    onValueChanged: (Float) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onValueChanged((value - steps).coerceIn(valueRange)) }) {
            Icon(imageVector = Icons.Default.RemoveCircleOutline, contentDescription = "remove")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = valueRepresentation(value))
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { onValueChanged((value + steps).coerceIn(valueRange)) }) {
            Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = "add")
        }
    }
}