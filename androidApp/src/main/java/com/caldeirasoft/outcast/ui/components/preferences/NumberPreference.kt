package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.caldeirasoft.outcast.domain.model.NumberPreferenceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.Integer.parseInt

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun NumberPreference(
    title: String,
    icon: ImageVector? = null,
    singleLineTitle: Boolean = false,
    enabled: Boolean = true,
    value: Int,
    valueRepresentation: @Composable (Int) -> String,
    onValueChanged: (Int) -> Unit,
) {
    val currentValue = remember(value) { mutableStateOf(value) }
    val showDialog = remember { mutableStateOf(false) }
    val closeDialog = { showDialog.value = false }

    Preference(
        title = title,
        singleLineTitle = singleLineTitle,
        enabled = enabled,
        icon = icon,
        summary = valueRepresentation(currentValue.value),
        onClick = { showDialog.value = true },
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { closeDialog() },
            title = { Text(text = title) },
            text = {
                TextField(
                    value = currentValue.value.toString(),
                    onValueChange = {
                        if (enabled) {
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