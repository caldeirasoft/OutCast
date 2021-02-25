package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.ui.util.toast
import timber.log.Timber

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
    LocalContext.current.toast("${t.message}")
    Timber.e(t)

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
            .preferredHeight(48.dp)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            title,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.h6
        )
    }
}

@Composable
fun StoreHeadingSectionWithLink(title:String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .preferredHeight(48.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            title,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.h6
        )
        Icon(imageVector = Icons.Filled.ArrowForward,
            contentDescription = "show all",
            modifier = Modifier
                .align(Alignment.CenterVertically))
    }
}


@Preview
@Composable
fun PreviewStoreHeadingSection() {
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
fun PreviewStoreHeadingSectionWithLink() {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxWidth()
    ) {
        StoreHeadingSectionWithLink("Nouveautés et tendances", { })
    }
}