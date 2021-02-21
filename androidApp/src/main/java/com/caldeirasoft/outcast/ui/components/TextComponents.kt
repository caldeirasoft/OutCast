package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.AmbientTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
    style: TextStyle = AmbientTextStyle.current
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
            if (result.didOverflowHeight && fontSize.value > minFontSize.value) {
                fontSize = fontSize * 0.9f
            }
        },
        style
    )
}

@Composable
fun OverflowText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
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
    style: TextStyle = AmbientTextStyle.current
) {
    var wasMeasured by remember { mutableStateOf(false) }
    var didOverflowHeight by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text,
            modifier = modifier.then(if (didOverflowHeight) Modifier.padding(bottom = 36.dp) else Modifier),
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
            maxLines = if (isExpanded) Integer.MAX_VALUE else maxLines,
            onTextLayout = { result ->
                if (wasMeasured.not()) {
                    wasMeasured = true
                    didOverflowHeight = result.didOverflowHeight
                }
            },
            style
        )

        if (didOverflowHeight && isExpanded.not()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .preferredHeight(60.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        brush = Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            0.4f to MaterialTheme.colors.background,
                            startY = 0.0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            {
                // text button "more..."
                TextButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    //contentPadding = ButtonDefaults.TextButtonContentPadding.copy(top = 0.dp, bottom = 0.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = { isExpanded = isExpanded.not() })
                {
                    Text(text = stringResource(id = R.string.action_show_more),
                    style = typography.button.copy(letterSpacing = 0.25.sp))
                }
            }
        }
    }
}
