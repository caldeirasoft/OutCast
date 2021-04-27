package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.model.KeyPreferenceItem
import com.caldeirasoft.outcast.domain.model.PreferenceItem

@ExperimentalMaterialApi
@Composable
fun Preference(
    item: PreferenceItem,
    summary: String? = null,
    onClick: () -> Unit = { },
    trailing: @Composable (() -> Unit)? = null,
) {
    StatusWrapper(enabled = item.enabled) {
        ListItem(
            text = {
                Text(text = item.title,
                    maxLines = if (item.singleLineTitle) 1 else Int.MAX_VALUE)
            },
            secondaryText = {
                (summary ?: item.summary)
                    .takeIf { it.isNotEmpty() }
                    ?.let {
                        Text(text = it)
                    }
            },
            icon = { PreferenceIcon(imageVector = item.icon) },
            modifier = Modifier.clickable(onClick = { if (item.enabled) onClick() }),
            trailing = trailing,
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun Preference(
    item: KeyPreferenceItem<*>,
    summary: @Composable () -> Unit,
    onClick: () -> Unit = { },
    trailing: @Composable (() -> Unit)? = null,
) {
    StatusWrapper(enabled = item.enabled) {
        ListItem(
            text = {
                Text(text = item.title,
                    maxLines = if (item.singleLineTitle) 1 else Int.MAX_VALUE)
            },
            secondaryText = summary,
            icon = { PreferenceIcon(imageVector = item.icon) },
            modifier = Modifier.clickable(onClick = { if (item.enabled) onClick() }),
            trailing = trailing,
        )
    }
}

@Composable
private fun PreferenceIcon(imageVector: ImageVector?) {
    val iconModifier = Modifier
        .padding(8.dp)
        .size(24.dp)

    if (imageVector != null) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = iconModifier
        )
    } else {
        Spacer(modifier = iconModifier)
    }
}


@Composable
fun StatusWrapper(enabled: Boolean = true, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalContentAlpha provides if (enabled) ContentAlpha.high else ContentAlpha.disabled) {
        content()
    }
}