package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.model.*
import com.caldeirasoft.outcast.ui.components.ScaffoldWithLargeHeaderAndLazyColumn
import com.caldeirasoft.outcast.ui.screen.podcastsettings.PodcastSettingsViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun PreferenceScreen(
    items: List<BasePreferenceItem>,
    title: String,
    navigateUp: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    ScaffoldWithLargeHeaderAndLazyColumn(
        title = title,
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        navigateUp = navigateUp,
    ) {
        items(items = items) { item ->
            MatchPreferenceItem(scope, item)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun MatchPreferenceItem(
    scope: CoroutineScope,
    item: BasePreferenceItem,
) {
    if (item is PreferenceItem) {
        val dependencyValue = item.dependencyValue
        if (dependencyValue != null && dependencyValue != true)
            return
    }

    when (item) {
        is PreferenceGroupItem -> {
            Text(
                text = item.title,
                fontSize = 14.sp,
                color = MaterialTheme.colors.secondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
            )

            item.items.forEach { child ->
                MatchPreferenceItem(scope, child)
            }
        }
        is EmptyPreferenceItem -> {
            Preference(item = item, onClick = item.onClick)
        }
        is SwitchPreferenceItem -> {
            SwitchPreference(
                item = item,
                value = item.value,
            )
        }
        is SingleListPreferenceItem<*> -> {
            ListPreference(
                item = item,
            )
        }
        is MultiListPreferenceItem<*> -> {
            MultiSelectListPreference(
                item = item,
            )
        }
        is SeekbarFloatPreferenceItem -> {
            SeekBarPreference(
                item = item,
            )
        }
        is NumberPreferenceItem -> {
            NumberPreference(
                item = item,
                value = item.value,
            )
        }
        is NumberRangeFloatPreferenceItem -> {
            NumberRangePreference(
                item = item,
            )
        }
        is NumberRangeIntPreferenceItem -> {
            NumberRangePreference(
                item = item,
            )
        }
    }
}
