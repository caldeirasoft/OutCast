package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.model.ActionPreferenceItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun ActionPreference(
    item: ActionPreferenceItem,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null,
) {
    StatusWrapper(enabled = item.enabled) {
        ListItem(
            text = {
                Text(text = item.title,
                    maxLines = if (item.singleLineTitle) 1 else Int.MAX_VALUE)
            },
            icon = {
                Icon(imageVector = item.icon,
                    null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp))
            },
            modifier = Modifier.clickable(onClick = { if (item.enabled) onClick() }),
            trailing = trailing,
        )
    }
}