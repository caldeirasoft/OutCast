package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.caldeirasoft.outcast.domain.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun PreferenceScreen(
    dataStore: DataStore<Preferences>,
    items: List<BasePreferenceItem>,
) {
    val scope = rememberCoroutineScope()
    val prefs by dataStore.data.collectAsState(initial = null)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = items) { item ->
            MatchPreferenceItem(scope, dataStore, prefs, item)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun MatchPreferenceItem(
    scope: CoroutineScope,
    dataStore: DataStore<Preferences>,
    prefs: Preferences?,
    item: BasePreferenceItem,
) {
    if (item is PreferenceItem) {
        val dependencyKey = item.dependencyKey
        if (dependencyKey != null && prefs?.get(dependencyKey) != true)
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
                MatchPreferenceItem(scope, dataStore, prefs, child)
            }
        }
        is EmptyPreferenceItem -> {
            Preference(item = item, onClick = item.onClick)
        }
        is SwitchPreferenceItem -> {
            SwitchPreference(
                item = item,
                value = prefs?.get(item.prefKey),
                onValueChanged = { newValue ->
                    scope.launch(Dispatchers.IO) {
                        dataStore.edit {
                            it[item.prefKey] = newValue
                        }
                    }
                }
            )
        }
        is SingleListPreferenceItem -> {
            ListPreference(
                item = item,
                value = prefs?.get(item.prefKey),
                onValueChanged = { newValue ->
                    scope.launch(Dispatchers.IO) {
                        dataStore.edit {
                            it[item.prefKey] = newValue
                        }
                    }
                })
        }
        is MultiListPreferenceItem -> {
            MultiSelectListPreference(
                item = item,
                values = prefs?.get(item.prefKey),
                onValuesChanged = { newValues ->
                    scope.launch(Dispatchers.IO) {
                        dataStore.edit {
                            it[item.prefKey] = newValues
                        }
                    }
                }
            )
        }
        is SeekbarFloatPreferenceItem -> {
            SeekBarPreference(
                item = item,
                value = prefs?.get(item.prefKey),
                onValueChanged = { newValue ->
                    scope.launch(Dispatchers.IO) {
                        dataStore.edit {
                            it[item.prefKey] = newValue
                        }
                    }
                },
            )
        }
        is NumberPreferenceItem -> {
            NumberPreference(
                item = item,
                value = prefs?.get(item.prefKey),
                onValueChanged = { newValue ->
                    scope.launch(Dispatchers.IO) {
                        dataStore.edit {
                            it[item.prefKey] = newValue
                        }
                    }
                },
            )
        }
        is NumberRangeFloatPreferenceItem -> {
            NumberRangePreference(
                item = item,
                value = prefs?.get(item.prefKey),
                onValueChanged = { newValue ->
                    scope.launch(Dispatchers.IO) {
                        dataStore.edit {
                            it[item.prefKey] = newValue
                        }
                    }
                },
            )
        }
    }
}
