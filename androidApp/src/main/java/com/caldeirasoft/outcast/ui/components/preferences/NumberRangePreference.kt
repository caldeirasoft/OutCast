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
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.model.NumberRangeFloatPreferenceItem
import com.caldeirasoft.outcast.domain.model.NumberRangeIntPreferenceItem
import com.caldeirasoft.outcast.domain.model.NumberRangePreferenceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
fun NumberRangePreference(
    item: NumberRangeFloatPreferenceItem
) {
    val currentValue = remember { mutableStateOf(item.value) }
    Preference(
        item = item,
        summary = item.summary,
        trailing = {
            PreferenceTrailing(
                item = item,
                value = currentValue.value,
                onValueMinusStep = { value, step -> value - step },
                onValuePlusStep = { value, step -> value + step },
                onValueChanged = {
                    currentValue.value = it
                    item.onValueChanged(currentValue.value)
                },
            )
        }
    )
}

@Composable
fun NumberRangePreference(
    item: NumberRangeIntPreferenceItem
) {
    val currentValue = remember { mutableStateOf(item.value) }
    Preference(
        item = item,
        summary = item.summary,
        trailing = {
            PreferenceTrailing(
                item = item,
                value = currentValue.value,
                onValueMinusStep = { value, step -> value - step },
                onValuePlusStep = { value, step -> value + step },
                onValueChanged = {
                    currentValue.value = it
                    item.onValueChanged(currentValue.value)
                },
            )
        }
    )
}

@Composable
private fun <T : Comparable<T>> PreferenceTrailing(
    item: NumberRangePreferenceItem<T>,
    value: T,
    onValueMinusStep: (T, T) -> T,
    onValuePlusStep: (T, T) -> T,
    onValueChanged: (T) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onValueChanged(onValueMinusStep(value, item.steps).coerceIn(item.valueRange)) }) {
            Icon(imageVector = Icons.Default.RemoveCircleOutline, contentDescription = "remove")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = item.valueRepresentation(value))
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { onValueChanged(onValuePlusStep(value, item.steps).coerceIn(item.valueRange)) }) {
            Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = "add")
        }
    }
}