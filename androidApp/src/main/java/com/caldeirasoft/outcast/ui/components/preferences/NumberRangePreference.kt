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
import com.caldeirasoft.outcast.domain.model.NumberRangePreferenceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun NumberRangePreference(
    item: NumberRangePreferenceItem,
    value: Float?,
    onValueChanged: (Float) -> Unit,
) {
    val currentValue = remember(value) { mutableStateOf(value ?: item.defaultValue) }
    Preference(
        item = item,
        summary = {
        },
        trailing = {
            PreferenceTrailing(
                item = item,
                value = currentValue.value,
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
    item: NumberRangePreferenceItem,
    value: Float,
    onValueChanged: (Float) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onValueChanged((value - item.steps).coerceIn(item.valueRange)) }) {
            Icon(imageVector = Icons.Default.RemoveCircleOutline, contentDescription = "remove")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = item.valueRepresentation(value))
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { onValueChanged((value + item.steps).coerceIn(item.valueRange)) }) {
            Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = "add")
        }
    }
}