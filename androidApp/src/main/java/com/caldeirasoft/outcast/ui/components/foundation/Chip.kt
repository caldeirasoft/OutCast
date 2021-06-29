package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CircleNotifications
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.ui.util.applyTextStyleCustom
import kotlinx.coroutines.launch

@Composable
fun ChipButton(
    selected: Boolean,
    onClick: () -> Unit,
    text: @Composable () -> Unit
) {
    val backgroundColor: Color = when {
        selected -> MaterialTheme.colors.primary.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    val contentColor: Color = when {
        selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }
    OutlinedButton(
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = animateColorAsState(backgroundColor).value,
            contentColor = animateColorAsState(contentColor).value,
            disabledContentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.disabled)
        ),
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(0.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 4.dp),
        onClick = onClick
    ) {
        val styledText = applyTextStyleCustom(typography.body2, ContentAlpha.high, text)
        styledText()
    }
}

@Composable
fun Chip(
    selected: Boolean,
    onClick: () -> Unit,
    isThumbnailVisible: Boolean = false,
    thumbnail: @Composable () -> Unit = {},
    text: @Composable () -> Unit
) {
    val backgroundColor: Color = when {
        selected -> MaterialTheme.colors.primary.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    val contentColor: Color = when {
        selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }
    val startPadding = if (isThumbnailVisible) 4.dp else 24.dp
    OutlinedButton(
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = animateColorAsState(backgroundColor).value,
            contentColor = animateColorAsState(contentColor).value,
            disabledContentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.disabled)
        ),
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(0.dp),
        contentPadding = PaddingValues(start = startPadding, end = 24.dp, top = 4.dp, bottom = 4.dp),
        onClick = onClick
    ) {
        if (isThumbnailVisible) {
            thumbnail()
            Spacer(modifier = Modifier.padding(8.dp))
        }
        val styledText = applyTextStyleCustom(typography.body2, ContentAlpha.high, text)
        styledText()
    }
}

@Composable
fun ChipImageButton(
    selected: Boolean,
    onClick: () -> Unit,
    image: @Composable () -> Unit,
    text: @Composable () -> Unit
) {
    val backgroundColor: Color = when {
        selected -> MaterialTheme.colors.primary.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    val contentColor: Color = when {
        selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }
    OutlinedButton(
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = animateColorAsState(backgroundColor).value,
            contentColor = animateColorAsState(contentColor).value,
            disabledContentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.disabled)
        ),
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(0.dp),
        contentPadding = PaddingValues(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        onClick = onClick
    ) {
        image()
        Spacer(modifier = Modifier.padding(8.dp))
        val styledText = applyTextStyleCustom(typography.body2, ContentAlpha.high, text)
        styledText()
    }
}

@Composable
fun ChipButtonBorderless(
    selected: Boolean,
    onClick: () -> Unit,
    text: @Composable () -> Unit
) {
    val backgroundColor: Color = when {
        selected -> MaterialTheme.colors.primary.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    val contentColor: Color = when {
        selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }
    OutlinedButton(
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = animateColorAsState(backgroundColor).value,
            contentColor = animateColorAsState(contentColor).value,
            disabledContentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.disabled)
        ),
        border = if (selected) ButtonDefaults.outlinedBorder else null,
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(0.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 4.dp),
        onClick = onClick
    ) {
        val styledText = applyTextStyleCustom(typography.body2, ContentAlpha.high, text)
        styledText()
    }
}

@Composable
fun <T: Any> ChipRadioSelector(
    selectedValue: T,
    values: Array<T>,
    onClick: (T) -> Unit,
    text: @Composable (T) -> Unit
) {
    Surface(
        //color = MaterialTheme.colors.onSurface.copy(alpha = 0.48f),
        border = ButtonDefaults.outlinedBorder,
        shape = RoundedCornerShape(50),
    ) {
        Row {
            values.forEach { item ->
                ChipButtonBorderless(
                    selected = (item == selectedValue),
                    onClick = { onClick(item) })
                {
                    text(item)
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T : Any> ChipGroup(
    modifier: Modifier = Modifier,
    selectedValue: T?,
    values: List<T>,
    onClick: (T?) -> Unit,
    isThumbnailVisible: Boolean = false,
    thumbnail: @Composable (T) -> Unit = {},
    text: @Composable (T) -> Unit,
) {
    ChipGroup(
        selectedValue = selectedValue,
        values = values, onClick = onClick
    ) { value, isSelected ->
        Chip(
            selected = isSelected,
            onClick = {
                if (!isSelected) onClick(value)
                else onClick(null)
            },
            isThumbnailVisible = isThumbnailVisible,
            text = {
                val styledText =
                    applyTextStyleCustom(typography.body2, ContentAlpha.high) {
                        text(value)
                    }
                styledText()
            },
            thumbnail = { thumbnail(value) }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T : Any> ChipGroup(
    modifier: Modifier = Modifier,
    selectedValue: T?,
    values: List<T>,
    onClick: (T?) -> Unit,
    itemContent: @Composable (T, Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        selectedValue?.let {
            item {
                OutlinedButton(
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                        disabledContentColor = MaterialTheme.colors.onSurface
                            .copy(alpha = ContentAlpha.disabled)
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(0.dp)
                        .widthIn(36.dp),
                    contentPadding = PaddingValues(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                    onClick = { onClick(null) }
                ) {
                    Icon(imageVector = Icons.Outlined.Clear, contentDescription = null)
                }
            }
        }
        items(items = values
            .filter { value -> selectedValue?.let { value == selectedValue } ?: true },
            key = { value -> value }
        ) { value ->
            itemContent(value, selectedValue?.let { value == selectedValue } ?: false)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T : Any> ChipImageGroup(
    modifier: Modifier = Modifier,
    selectedValue: T?,
    values: List<T>,
    onClick: (T?) -> Unit,
    image: @Composable (T) -> Unit,
    text: @Composable (T) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        selectedValue?.let {
            item {
                OutlinedButton(
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                        disabledContentColor = MaterialTheme.colors.onSurface
                            .copy(alpha = ContentAlpha.disabled)
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(0.dp)
                        .widthIn(36.dp),
                    contentPadding = PaddingValues(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                    onClick = { onClick(null) }
                ) {
                    Icon(imageVector = Icons.Outlined.Clear, contentDescription = null)
                }
            }
        }
        items(items = values
            .filter { value -> selectedValue?.let { value == selectedValue } ?: true },
            key = { value -> value }
        ) { value ->
            ChipImageButton(
                selected = selectedValue == value,
                onClick = {
                    if (selectedValue != value)
                        onClick(value)
                    else onClick(null)

                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                },
                image = {
                    image(value)
                }
            ) {
                val styledText =
                    applyTextStyleCustom(
                        typography.body2,
                        ContentAlpha.high
                    ) {
                        text(value)
                    }
                styledText()
            }
        }
    }
}

@Composable
fun ActionChipButton(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit
) {
    val backgroundColor: Color = when {
        selected -> MaterialTheme.colors.primary.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    val contentColor: Color = when {
        selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }
    OutlinedButton(
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = animateColorAsState(backgroundColor).value,
            contentColor = animateColorAsState(contentColor).value,
            disabledContentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.disabled)
        ),
        shape = RoundedCornerShape(50),
        modifier = modifier.padding(0.dp),
        contentPadding = PaddingValues(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier.padding(end = 4.dp)) {
            icon()
        }
        val styledText = applyTextStyleCustom(typography.body2, ContentAlpha.high, text)
        styledText()
    }
}

@Preview
@Composable
fun PreviewChipButton() {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxWidth()
    ) {
        ChipButton(
            selected = true,
            onClick = {},
        ) {
            Text(text = "Podcasts")
        }
    }
}

@Preview
@Composable
fun PreviewChipImageButton() {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxWidth()
    ) {
        ChipImageButton(
            selected = true,
            onClick = {},
            image = { Icon(imageVector = Icons.Default.CircleNotifications, contentDescription = null) }
        ) {
            Text(text = "Podcasts")
        }
    }
}

@Preview
@Composable
fun PreviewChipRadio() {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxWidth()
    ) {
        ChipRadioSelector(
            selectedValue = StoreItemType.EPISODE,
            values = StoreItemType.values(),
            onClick = {},
        ) {
            Text(text = when (it) {
                StoreItemType.PODCAST -> "Podcasts"
                StoreItemType.EPISODE -> "Episodes"
            })
        }
    }
}