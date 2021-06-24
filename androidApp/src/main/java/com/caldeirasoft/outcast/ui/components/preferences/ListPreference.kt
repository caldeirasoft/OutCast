package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.model.SingleListPreferenceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun <T> ListPreference(
    title: String,
    icon: ImageVector? = null,
    singleLineTitle: Boolean = false,
    enabled: Boolean = true,
    entries: Map<T, String>,
    value: T,
    onValueChanged: (T) -> Unit,
) {
    val showDialog = remember { mutableStateOf(false) }
    val closeDialog = { showDialog.value = false }

    Preference(
        title = title,
        singleLineTitle = singleLineTitle,
        enabled = enabled,
        icon = icon,
        summary = entries[value],
        onClick = { showDialog.value = true },
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { closeDialog() },
            title = { Text(text = title) },
            text = {
                Column {
                    entries.forEach { current ->
                        val isSelected = value == current.key
                        val onSelected = {
                            onValueChanged(current.key)
                            closeDialog()
                        }
                        Row(Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = isSelected,
                                onClick = { if (!isSelected) onSelected() }
                            )
                            .padding(16.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { if (!isSelected) onSelected() }
                            )
                            Text(
                                text = current.value,
                                style = MaterialTheme.typography.body1.merge(),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = { }
        )
    }
}