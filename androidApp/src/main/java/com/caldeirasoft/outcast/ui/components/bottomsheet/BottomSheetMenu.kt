package com.caldeirasoft.outcast.ui.components.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun CoroutineScope.OpenBottomSheetMenu(
    header: @Composable () -> Unit,
    items: List<BaseBottomSheetMenuItem>,
    drawerState: ModalBottomSheetState,
    drawerContent: ModalBottomSheetContent,
)
{
    drawerContent.updateContent {
        BottomSheetMenu(
            header = header,
            items = items,
            drawerState = drawerState
        )
    }
    this.launch {
        delay(500)
        drawerState.show()
    }
}

@Composable
private fun BottomSheetMenu(
    header: @Composable () -> Unit,
    items: List<BaseBottomSheetMenuItem>,
    drawerState: ModalBottomSheetState,
) {
    val coroutineScope = rememberCoroutineScope()
    Column()
    {
        // header
        header()
        Divider()
        items.forEach { item ->
            when (item) {
                is BottomSheetMenuItem ->
                    MenuItem(text = stringResource(id = item.titleId),
                        imageVector = item.icon,
                        click = {
                            item.onClick()
                            coroutineScope.launch { drawerState.hide() }
                        }
                    )
                is BottomSheetSeparator ->
                    Divider()
            }
        }
        Spacer(modifier = Modifier.height(56.dp))
    }
}

@Composable
private fun MenuItem(text: String, imageVector: ImageVector?, click: () -> Unit) {
    Row(
        Modifier
            .heightIn(min = MinHeight)
            .clickable { click() }) {
        Box(
            Modifier
                .align(Alignment.CenterVertically)
                .widthIn(min = IconLeftPadding + IconMinPaddedWidth)
                .padding(
                    start = IconLeftPadding,
                    top = IconVerticalPadding,
                    bottom = IconVerticalPadding
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            val tintColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
            imageVector?.let {
                Icon(imageVector = imageVector,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = tintColor
                )
            }
        }
        Box(
            Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(start = ContentLeftPadding, end = ContentRightPadding),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = text,
            )
        }
    }
}

private val MinHeight = 48.dp
// Icon related defaults.
private val IconMinPaddedWidth = 32.dp
private val IconLeftPadding = 16.dp
private val IconVerticalPadding = 8.dp

// Content related defaults.
private val ContentLeftPadding = 12.dp
private val ContentRightPadding = 16.dp