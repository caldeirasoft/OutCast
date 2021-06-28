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
) {
    val currentValue = value ?: item.value
    Preference(
        item = item,
        onClick = { item.onValueChanged(!currentValue) }
    ) {
        Switch(
            checked = currentValue,
            onCheckedChange = { item.onValueChanged(!currentValue) },
            enabled = item.enabled
        )
    }
}