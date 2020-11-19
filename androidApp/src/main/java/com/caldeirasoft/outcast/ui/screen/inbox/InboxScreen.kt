package com.caldeirasoft.outcast.ui.screen.inbox

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.ambient.InboxViewModelAmbient

@Composable
fun InboxScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Inbox")
                },
                actions = {
                    IconButton(onClick = ActionsAmbient.current.navigateToDiscover) {
                        Icon(asset = Icons.Filled.Favorite)
                    }
                })
        }
    )
    { innerPadding ->
        InboxContent(
            modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun InboxContent(
    modifier: Modifier = Modifier
) {
    val model = InboxViewModelAmbient.current
    Column(modifier = modifier.padding(16.dp)) {
        Text("Hi there!")
        Text("Thanks for going through the Layouts codelab")
        Text(model.textData)
    }
}