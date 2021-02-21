package com.caldeirasoft.outcast.ui.screen.inbox

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.presentation.viewmodel.InboxViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun InboxScreen(viewModel: InboxViewModel = getViewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Inbox")
                },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(imageVector = Icons.Filled.Favorite)
                    }
                })
        }
    )
    { innerPadding ->
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Hi there!")
            Text("Thanks for going through the Layouts codelab")
            Text(viewModel.textData)
        }
    }
}