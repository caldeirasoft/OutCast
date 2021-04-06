package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import com.caldeirasoft.outcast.domain.model.SwitchPreferenceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun SwitchPreference(
    item: SwitchPreferenceItem,
    value: Boolean?,
    onValueChanged: (Boolean) -> Unit,
) {
    val currentValue = value ?: item.defaultValue
    Preference(
        item = item,
        onClick = { onValueChanged(!currentValue) }
    ) {
        Switch(
            checked = currentValue,
            onCheckedChange = { onValueChanged(!currentValue) },
            enabled = item.enabled
        )
    }
}