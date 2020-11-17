package com.caldeirasoft.outcast.ui.screen.inbox

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.caldeirasoft.outcast.presentation.viewmodel.InboxViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.java.KoinJavaComponent.inject

@Composable
fun InboxScreen(
    scope: CoroutineScope,
    viewModel: InboxViewModel,
    navigateToDiscover: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Inbox")
                },
                actions = {
                    IconButton(onClick = navigateToDiscover) {
                        Icon(asset = Icons.Filled.Favorite)
                    }
                })
        }
    )
    { innerPadding ->
        InboxContent(
            model = viewModel,
            modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun InboxContent(
    model: InboxViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Hi there!")
        Text("Thanks for going through the Layouts codelab")
        Text(model.textData)
    }
}