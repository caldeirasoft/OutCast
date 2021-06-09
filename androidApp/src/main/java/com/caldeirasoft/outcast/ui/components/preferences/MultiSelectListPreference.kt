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
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.model.MultiListPreferenceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun <T> MultiSelectListPreference(
    item: MultiListPreferenceItem<T>,
) {
    val selectedValues = item.defaultValue
    val showDialog = remember { mutableStateOf(false) }
    val closeDialog = { showDialog.value = false }
    val description = item.entries
        .filter { selectedValues.contains(it.key) }
        .map { it.value }
        .joinToString(separator = ", ", limit = 3)

    Preference(
        item = item,
        summary = if (description.isNotBlank()) description else null,
        onClick = { showDialog.value = true }
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { closeDialog() },
            title = { Text(text = item.title) },
            text = {
                Column {
                    item.entries.forEach { current ->
                        val isSelected = selectedValues.contains(current.key)
                        val onSelectionChanged = {
                            val result = when (!isSelected) {
                                true -> selectedValues + current.key
                                false -> selectedValues - current.key
                            }
                            item.onValueChanged(result)
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