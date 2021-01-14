package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.viewModel
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage
import com.caldeirasoft.outcast.ui.components.ErrorScreen
import com.caldeirasoft.outcast.ui.components.LoadingScreen
import com.caldeirasoft.outcast.ui.components.StoreEpisodeListItem
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.util.onError
import com.caldeirasoft.outcast.ui.util.onLoading
import com.caldeirasoft.outcast.ui.util.onSuccess
import com.caldeirasoft.outcast.ui.util.viewModelProviderFactoryOf
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun StorePodcastScreen(
    url: String,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit
) {
    val viewModel: StorePodcastViewModel = viewModel(
        key = url,
        factory = viewModelProviderFactoryOf { StorePodcastViewModel(url) }
    )
    val viewState by viewModel.state.collectAsState()

    StorePodcastScreen(
        viewState = viewState,
        navigateToPodcast = navigateToPodcast,
        navigateUp = navigateUp)
}

@Composable
private fun StorePodcastScreen(
    viewState: StorePodcastViewModel.State,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = viewState.storeData?.name.orEmpty())
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                },
                actions = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Filled.HourglassFull)
                    }
                })
        }
    )
    {
        viewState
            .screenState
            .onLoading { LoadingScreen() }
            .onError { ErrorScreen(t = it) }
            .onSuccess {
                val storeData = viewState.storeData
                storeData?.let {
                    LazyColumn {
                        item {
                            Greeting()
                        }
                        item {
                            StorePodcastHeader(storePodcastData = storeData)
                        }
                        items(storeData.episodes) { storeEpisodeItem ->
                            StoreEpisodeListItem(episode = storeEpisodeItem)
                            Divider()
                        }
                    }
                }
            }
    }
}

@Composable
private fun StorePodcastHeader(
    storePodcastData: StorePodcastPage
) {
    val context = AmbientContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(152.dp)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Card(
            backgroundColor = Color.Transparent,
            shape = RoundedCornerShape(8.dp),
        ) {
            CoilImage(
                imageModel = storePodcastData.getArtworkUrl(),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .preferredWidth(120.dp)
                    .aspectRatio(1f)
            )
        }
        Column(modifier = Modifier
            .padding(start = 16.dp)
            .fillMaxHeight()) {
            Box(modifier = Modifier
                .weight(1f)
                .background(Color.Yellow)) {
                Text(
                    storePodcastData.name,
                    modifier = Modifier
                        .align(Alignment.CenterStart),
                    style = MaterialTheme.typography.h5,
                    maxLines = 2,
                    color = Color.getColor(storePodcastData.artwork?.textColor1!!)
                )
            }
            Text(
                storePodcastData.artistName,
                modifier = Modifier
                    .padding(bottom = 4.dp),
                style = MaterialTheme.typography.body1,
                maxLines = 2,
                color = Color.getColor(storePodcastData.artwork?.textColor2!!)
            )
        }
    }
}

@Composable
private fun StorePodcastContent(
    storePodcastData: StorePodcastPage
) {
    LazyColumnFor(items = storePodcastData.episodes) { item ->
        StoreEpisodeListItem(episode = item)
        Divider()
    }
}

@Composable
fun Greeting() {
    var width by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }
    var fontSize by remember { mutableStateOf(35) }
    val msg =
        "My really long long long long long text that needs to be resized to the height of this Column. My really long long long long long text that needs to be resized to the height of this Column. My really long long long long long text that needs to be resized to the height of this Column. My really long long long long long text "
    Column(modifier = Modifier
        .height(100.dp)
        .padding(8.dp)
        .background(Color.Blue)
        .onGloballyPositioned {
            width = it.size.width
            height = it.size.height
        }) {
        Log.d("mainactivity", "width = $width")
        Log.d("mainactivity", "height = $height")
        Text(
            modifier = Modifier.background(Color.Green).fillMaxHeight(),
            style = TextStyle(fontSize = fontSize.sp),
            text = msg,
            onTextLayout = { result ->
                Log.d("mainactivity", "lines = ${result.lineCount}")
                Log.d("mainactivity", "size = ${result.size}")
                Log.d("mainactivity", "didOverflowHeight = ${result.didOverflowHeight}")
                if (result.didOverflowHeight && fontSize > 25) {
                    fontSize = (fontSize * 0.8f).toInt()
                    Log.d("mainactivity", "fontSize = ${fontSize}")
                }
            }
        )
    }
}

fun calculateFontSize(msg: String, height: Int): Int {
    return height / (msg.length / 8)
}

