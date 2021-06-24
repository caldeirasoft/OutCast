package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.caldeirasoft.outcast.domain.model.SwitchPreferenceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun SwitchPreference(
    title: String,
    summary: String,
    icon: ImageVector? = null,
    singleLineTitle: Boolean = false,
    enabled: Boolean = true,
    value: Boolean = false,
    onValueChanged: (Boolean) -> Unit,
) {
    val currentValue = value
    Preference(
        title = title,
        summary = summary,
        singleLineTitle = singleLineTitle,
        enabled = enabled,
        icon = icon,
        onClick = { onValueChanged(!currentValue) }
    ) {
        Switch(
            checked = currentValue,
            onCheckedChange = { onValueChanged(!currentValue) },
            enabled = enabled
        )
    }
}