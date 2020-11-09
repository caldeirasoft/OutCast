package com.caldeirasoft.outcast.ui.screen.inbox

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate

@Composable
fun InboxScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Inbox")
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("podcasts")
                    }) {
                        Icon(asset = Icons.Filled.Favorite)
                    }
                })
        }
    )
    { innerPadding ->
        InboxContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun InboxContent(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Hi there!")
        Text("Thanks for going through the Layouts codelab")
    }
}