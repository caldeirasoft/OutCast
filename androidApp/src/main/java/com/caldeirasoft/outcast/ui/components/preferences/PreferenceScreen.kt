package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.caldeirasoft.outcast.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun PreferenceScreen(dataStore: DataStore<Preferences>, items: List<BasePreferenceItem>) {
    val scope = rememberCoroutineScope()

    val prefs by dataStore.data.collectAsState(initial = null)

    LazyColumn {
        items(items = items) { item ->
            when (item) {
                is SwitchPreferenceItem -> {
                    SwitchPreference(
                        item = item,
                        value = prefs?.get(item.prefKey),
                        onValueChanged = { newValue ->
                            scope.launch(Dispatchers.IO) {
                                dataStore.edit { it[item.prefKey] = newValue }
                            }
                        }
                    )
                }
                is SingleListPreferenceItem -> {
                    ListPreference(
                        item = item,
                        value = prefs?.get(item.prefKey),
                        onValueChanged = { newValue ->
                            scope.launch { dataStore.edit { it[item.prefKey] = newValue } }
                        })
                }
                is MultiListPreferenceItem -> {
                    MultiSelectListPreference(
                        item = item,
                        values = prefs?.get(item.prefKey),
                        onValuesChanged = { newValues ->
                            scope.launch { dataStore.edit { it[item.prefKey] = newValues } }
                        }
                    )
                }
                is SeekbarPreferenceItem -> {
                    SeekBarPreference(
                        item = item,
                        value = prefs?.get(item.prefKey),
                        onValueChanged = { newValue ->
                            scope.launch {
                                dataStore.edit { it[item.prefKey] = newValue }
                            }
                        },
                    )
                }
            }
        }
    }
}