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
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.model.SeekbarFloatPreferenceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun SeekBarPreference(
    item: SeekbarFloatPreferenceItem,
) {
    val currentValue = remember { mutableStateOf(item.value) }
    Preference(
        item = item,
        summary = {
            SeekBarPreferenceSummary(
                item = item,
                sliderValue = currentValue.value,
                onValueChanged = { currentValue.value = it },
                onValueChangeFinished = { item.onValueChanged(currentValue.value) }
            )
        },
    )
}

@Composable
private fun SeekBarPreferenceSummary(
    item: SeekbarFloatPreferenceItem,
    sliderValue: Float,
    onValueChanged: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Column {
        Text(text = item.summary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = item.valueRepresentation(sliderValue))
            Spacer(modifier = Modifier.width(16.dp))
            Slider(
                value = sliderValue,
                onValueChange = { if (item.enabled) onValueChanged(it) },
                valueRange = item.valueRange,
                steps = item.steps,
                onValueChangeFinished = onValueChangeFinished
            )
        }
    }
}