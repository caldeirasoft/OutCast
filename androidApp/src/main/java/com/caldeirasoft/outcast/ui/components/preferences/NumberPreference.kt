package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.caldeirasoft.outcast.domain.model.NumberPreferenceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.Integer.parseInt

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun NumberPreference(
    item: NumberPreferenceItem,
    value: Int?,
    onValueChanged: (Int) -> Unit,
) {
    val currentValue = remember(value) { mutableStateOf(value ?: item.defaultValue) }
    val showDialog = remember { mutableStateOf(false) }
    val closeDialog = { showDialog.value = false }

    Preference(
        item = item,
        summary = item.valueRepresentation(currentValue.value),
        onClick = { showDialog.value = true },
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { closeDialog() },
            title = { Text(text = item.title) },
            text = {
                TextField(
                    value = currentValue.value.toString(),
                    onValueChange = {
                        if (item.enabled) {
                            onValueChanged(parseInt(it))
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Default
                    )
                )
            },
            confirmButton = { }
        )
    }
}