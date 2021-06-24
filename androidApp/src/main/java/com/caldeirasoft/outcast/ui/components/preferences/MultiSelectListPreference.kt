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
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun <T> MultiSelectListPreference(
    title: String,
    icon: ImageVector? = null,
    singleLineTitle: Boolean = false,
    enabled: Boolean = true,
    entries: Map<T, String>,
    values: Set<T> = emptySet(),
    onValueChanged: (Set<T>) -> Unit,
) {
    val showDialog = remember { mutableStateOf(false) }
    val closeDialog = { showDialog.value = false }
    val description = entries
        .filter { values.contains(it.key) }
        .map { it.value }
        .joinToString(separator = ", ", limit = 3)

    Preference(
        title = title,
        singleLineTitle = singleLineTitle,
        enabled = enabled,
        icon = icon,
        summary = if (description.isNotBlank()) description else null,
        onClick = { showDialog.value = true }
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { closeDialog() },
            title = { Text(text = title) },
            text = {
                Column {
                    entries.forEach { current ->
                        val isSelected = values.contains(current.key)
                        val onSelectionChanged = {
                            val result = when (!isSelected) {
                                true -> values + current.key
                                false -> values - current.key
                            }
                            onValueChanged(result)
                        }
                        Row(Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = isSelected,
                                onClick = { onSelectionChanged() }
                            )
                            .padding(16.dp)
                        ) {
                            Checkbox(checked = isSelected, onCheckedChange = {
                                onSelectionChanged()
                            })
                            Text(
                                text = current.value,
                                style = MaterialTheme.typography.body1.merge(),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { closeDialog() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.secondary),
                ) {
                    Text(text = "Select")
                }
            }
        )
    }
}