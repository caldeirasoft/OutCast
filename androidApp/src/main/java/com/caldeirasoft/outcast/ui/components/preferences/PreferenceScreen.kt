package com.caldeirasoft.outcast.ui.components.preferences

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import com.caldeirasoft.outcast.domain.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun PreferenceScreen(
    prefs: Preferences?,
    viewModel: PreferenceViewModel,
    items: List<BasePreferenceItem>,
) {
    //val prefs by dataStore.data.collectAsState(initial = null)

    LazyColumn {
        items(items = items) { item ->
            when (item) {
                is SwitchPreferenceItem -> {
                    AnimatedVisibility(visible = item.visible) {
                        SwitchPreference(
                            item = item,
                            value = prefs?.get(item.prefKey),
                            onValueChanged = { newValue ->
                                viewModel.updatePreference(item.prefKey, newValue)
                            }
                        )
                    }
                }
                is SingleListPreferenceItem -> {
                    AnimatedVisibility(visible = item.visible) {
                        ListPreference(
                            item = item,
                            value = prefs?.get(item.prefKey),
                            onValueChanged = { newValue ->
                                viewModel.updatePreference(item.prefKey, newValue)
                            })
                    }
                }
                is MultiListPreferenceItem -> {
                    AnimatedVisibility(visible = item.visible) {
                        MultiSelectListPreference(
                            item = item,
                            values = prefs?.get(item.prefKey),
                            onValuesChanged = { newValues ->
                                viewModel.updatePreference(item.prefKey, newValues)
                            }
                        )
                    }
                }
                is SeekbarPreferenceItem -> {
                    SeekBarPreference(
                        item = item,
                        value = prefs?.get(item.prefKey),
                        onValueChanged = { newValue ->
                            viewModel.updatePreference(item.prefKey, newValue)
                        },
                    )
                }
                is NumberPreferenceItem -> {
                    NumberPreference(
                        item = item,
                        value = prefs?.get(item.prefKey),
                        onValueChanged = { newValue ->
                            viewModel.updatePreference(item.prefKey, newValue)
                        },
                    )
                }
                is NumberRangePreferenceItem -> {
                    AnimatedVisibility(visible = item.visible) {
                        NumberRangePreference(
                            item = item,
                            value = prefs?.get(item.prefKey),
                            onValueChanged = { newValue ->
                                viewModel.updatePreference(item.prefKey, newValue)
                            },
                        )
                    }
                }
                is ActionPreferenceItem -> {
                    AnimatedVisibility(visible = item.visible) {
                        ActionPreference(
                            item = item,
                            onClick = { item.action() })
                    }
                }
            }
        }
    }
}
