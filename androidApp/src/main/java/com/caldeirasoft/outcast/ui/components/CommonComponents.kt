package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.caldeirasoft.outcast.ui.util.toast

@Composable
fun LoadingScreen()
{
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(t: Throwable) {
    ContextAmbient.current.toast("${t.message}")

    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Failed to load team",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun StoreHeadingSection(title:String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            title,
            modifier = Modifier,
            style = MaterialTheme.typography.h6
        )
    }
}

@Composable
fun StoreHeadingSectionWithLink(title:String, onClick: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick)
    ) {
        val (text, icon) = createRefs()
        Text(
            title,
            modifier = Modifier
                .constrainAs(text) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    linkTo(start = parent.start, end = icon.start)
                    width = Dimension.fillToConstraints
                },
            style = MaterialTheme.typography.h6
        )
        Icon(imageVector = Icons.Filled.ArrowForward,
            modifier = Modifier
                .constrainAs(icon) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    linkTo(start = text.end, end = parent.end)
                })
    }
}


@Preview
@Composable
fun previewStoreHeadingSection() {
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    ) {
        StoreHeadingSection("Nouveautés et tendances")
    }
}

@Preview
@Composable
fun previewStoreHeadingSectionWithLink() {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxWidth()
    ) {
        StoreHeadingSectionWithLink("Nouveautés et tendances", { })
    }
}