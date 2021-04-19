/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.caldeirasoft.outcast.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.core.net.toUri
import kotlinx.coroutines.coroutineScope
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import timber.log.Timber

private const val TAG_URL = "url"

data class ResolvedLink(
    val expanded: String?,
    val skip: Boolean = false,
    val display: String? = null,
)

@Composable
fun HtmlText(
    modifier: Modifier = Modifier,
    htmlText: String,
    linkResolver: (href: String) -> ResolvedLink,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val context = LocalContext.current
    val textColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.body1.copy(color = textColor)
    ) {
        RenderContent(
            modifier = modifier,
            htmlText = htmlText,
            fontSize = fontSize,
            textAlign = textAlign,
            overflow = overflow,
            maxLines = maxLines,
            linkResolver = linkResolver,
            onTextLayout = onTextLayout,
            onLinkClicked = {
                Timber.d("Open link $it")
                //openWeb(context, it)
                //navigator.openLink(it)
            },
        )
    }
}

@Composable
private fun RenderContent(
    modifier: Modifier = Modifier,
    htmlText: String,
    linkResolver: (href: String) -> ResolvedLink,
    onLinkClicked: (String) -> Unit = {},
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val value = renderContentAnnotatedString(
        htmlText = htmlText,
        linkResolver = linkResolver,
    )
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    if (value.text.isNotEmpty() && value.text.isNotBlank()) {
        Text(
            modifier = modifier.pointerInput(Unit) {
                forEachGesture {
                    coroutineScope {
                        val change = awaitPointerEventScope {
                            awaitFirstDown()
                        }
                        val annotation =
                            layoutResult.value?.getOffsetForPosition(change.position)?.let {
                                value.getStringAnnotations(start = it, end = it)
                                    .firstOrNull()
                            }
                        if (annotation != null) {
                            change.consumeDownChange()
                            val up = awaitPointerEventScope {
                                waitForUpOrCancellation()?.also { it.consumeDownChange() }
                            }
                            if (up != null) {
                                onLinkClicked.invoke(annotation.item)
                            }
                        }
                    }
                }
            },
            text = value,
            fontSize = fontSize,
            textAlign = textAlign,
            overflow = overflow,
            maxLines = maxLines,
            onTextLayout = {
                layoutResult.value = it
                onTextLayout(it)
            }
        )
    }
}

@Composable
fun renderContentAnnotatedString(
    htmlText: String,
    linkResolver: (href: String) -> ResolvedLink,
): AnnotatedString {
    val textColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    val textStyle = MaterialTheme.typography.body1.copy(color = textColor)
    val linkStyle = textStyle.copy(MaterialTheme.colors.primary)
    val styleData = remember(textStyle, linkStyle) {
        StyleData(
            textStyle = textStyle,
            linkStyle = linkStyle,
        )
    }
    val renderContext = remember(linkResolver) {
        RenderContext(linkResolver = linkResolver)
    }
    return remember(
        htmlText,
        styleData,
    ) {
        val document = Jsoup.parse(htmlText.replace("\n", "<br>"))
        buildAnnotatedString {
            document.body().childNodes().forEach {
                RenderNode(it, renderContext, styleData)
            }
        }
    }
}

private data class RenderContext(
    val linkResolver: (href: String) -> ResolvedLink,
)

data class StyleData(
    val textStyle: TextStyle,
    val linkStyle: TextStyle,
)

private fun AnnotatedString.Builder.RenderNode(
    node: Node,
    context: RenderContext,
    styleData: StyleData,
) {
    when (node) {
        is Element -> {
            this.RenderElement(node, context = context, styleData = styleData)
        }
        is TextNode -> {
            RenderText(node.text(), styleData.textStyle)
        }
    }
}

private fun AnnotatedString.Builder.RenderText(text: String, textStyle: TextStyle) {
    pushStyle(
        textStyle.toSpanStyle()
    )
    append(text)
    pop()
}

private fun AnnotatedString.Builder.RenderElement(
    element: Element,
    context: RenderContext,
    styleData: StyleData,
) {
    if (!element.hasClass("invisible")) {
        when (element.normalName()) {
            "a" -> {
                RenderLink(element, context, styleData)
            }
            "br" -> {
                RenderText("\n", styleData.textStyle)
            }
            "span", "p" -> {
                element.childNodes().forEach {
                    RenderNode(node = it, context = context, styleData = styleData)
                }
            }
        }
    }
}

private fun AnnotatedString.Builder.RenderLink(
    element: Element,
    context: RenderContext,
    styleData: StyleData,
) {
    val href = element.attr("href")
    val resolvedLink = context.linkResolver.invoke(href)
    when {
        resolvedLink.expanded != null -> {
            pushStringAnnotation(TAG_URL, resolvedLink.expanded)
            RenderText(resolvedLink.display ?: resolvedLink.expanded, styleData.linkStyle)
            pop()
        }
        resolvedLink.skip -> {
        }
        else -> {
            pushStringAnnotation(TAG_URL, href)
            element.childNodes().forEach {
                RenderNode(
                    node = it,
                    context = context,
                    styleData = styleData.copy(textStyle = styleData.linkStyle)
                )
            }
            pop()
        }
    }
}

private fun openWeb(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
}