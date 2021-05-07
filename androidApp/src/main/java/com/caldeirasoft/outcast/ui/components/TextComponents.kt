package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.ui.theme.typography

@Composable
fun AutoSizedText(
    text: String,
    modifier: Modifier = Modifier,
    minFontSize: TextUnit,
    maxFontSize: TextUnit,
    color: Color = Color.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current
) {
    var fontSize by remember { mutableStateOf(maxFontSize) }
    Text(
        text,
        modifier,
        color,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        onTextLayout = { result ->
            if (result.didOverflowHeight && fontSize > minFontSize) {
                fontSize *= 0.9
            }
        },
        style
    )
}

@Composable
fun OverflowText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
) {
    var wasMeasured by remember { mutableStateOf(false) }
    var didOverflowHeight by remember { mutableStateOf(true) }
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier = modifier
        .fillMaxSize()
        .clickable { isExpanded = isExpanded.not() }) {
        Text(
            text = text,
            modifier = Modifier,
            fontSize = fontSize,
            textAlign = textAlign,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = if (isExpanded) Integer.MAX_VALUE else maxLines,
            onTextLayout = { result ->
                if (wasMeasured.not()) {
                    wasMeasured = true
                    didOverflowHeight = result.didOverflowHeight
                }
            },
        )

        if (didOverflowHeight && isExpanded.not()) {
            OverflowButton()
        }
    }
}

@Composable
fun OverflowHtmlText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    var wasMeasured by remember { mutableStateOf(false) }
    var didOverflowHeight by remember { mutableStateOf(true) }
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier = modifier
        .fillMaxSize()
        .then(if (!isExpanded) Modifier.clickable { isExpanded = isExpanded.not() } else Modifier)) {
        HtmlText(
            htmlText = text,
            modifier = Modifier,
            fontSize = fontSize,
            textAlign = textAlign,
            overflow = overflow,
            maxLines = if (isExpanded) Integer.MAX_VALUE else maxLines,
            onTextLayout = { result ->
                if (wasMeasured.not()) {
                    wasMeasured = true
                    didOverflowHeight = result.didOverflowHeight
                }
            },
            linkResolver = { href -> ResolvedLink(expanded = href) }
        )

        if (didOverflowHeight && isExpanded.not()) {
            OverflowButton()
        }
    }
}

@Composable
fun BoxScope.OverflowButton(
) {

    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .background(
                brush = Brush.horizontalGradient(
                    0.0f to Color.Transparent,
                    0.4f to MaterialTheme.colors.background,
                    startX = 0.0f,
                    endX = Float.POSITIVE_INFINITY
                )
            )
    )
    {
        // text button "more..."
        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = stringResource(id = R.string.action_more),
            style = typography.button.copy(letterSpacing = 0.25.sp))

    }
}
