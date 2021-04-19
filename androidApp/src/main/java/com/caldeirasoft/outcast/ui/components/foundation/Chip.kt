package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.ui.util.applyTextStyleCustom

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

@Composable
fun ActionChipButton(
    selected: Boolean,
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
        modifier = Modifier.padding(0.dp),
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