package com.caldeirasoft.outcast.domain.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.datastore.preferences.core.*
import kotlin.math.roundToInt

interface BasePreferenceItem {
    val title: String
    val enabled: Boolean
}

class PreferenceGroupItem(
    override val title: String,
    override val enabled: Boolean = true,
    val items: List<PreferenceItem>
) : BasePreferenceItem

sealed class PreferenceItem : BasePreferenceItem {
    abstract val summary: String
    abstract val singleLineTitle: Boolean
    abstract val icon: ImageVector?
    abstract val dependencyKey: Preferences.Key<Boolean>?
}

class EmptyPreferenceItem(
    override val title: String,
    override val summary: String,
    override val icon: ImageVector? = null,
    override val singleLineTitle: Boolean = false,
    override val enabled: Boolean = true,
    override val dependencyKey: Preferences.Key<Boolean>? = null,
    val onClick: () -> Unit = { },
) : PreferenceItem()

sealed class KeyPreferenceItem<T> : PreferenceItem() {
    abstract val prefKey: Preferences.Key<T>
}

sealed class ListPreferenceItem<T> : KeyPreferenceItem<T>() {
    abstract val entries: Map<String, String>
}

data class SwitchPreferenceItem(
    override val title: String,
    override val summary: String,
    override val prefKey: Preferences.Key<Boolean>,
    override val icon: ImageVector? = null,
    override val singleLineTitle: Boolean = false,
    override val enabled: Boolean = true,
    override val dependencyKey: Preferences.Key<Boolean>? = null,
    val defaultValue: Boolean = false,
) : KeyPreferenceItem<Boolean>()

data class SeekbarFloatPreferenceItem(
    override val title: String,
    override val summary: String,
    override val prefKey: Preferences.Key<Float>,
    override val icon: ImageVector? = null,
    override val singleLineTitle: Boolean = false,
    override val enabled: Boolean = true,
    override val dependencyKey: Preferences.Key<Boolean>? = null,
    val defaultValue: Float = 0F,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    val steps: Int = 0,
    val valueRepresentation: (Float) -> String,
) : KeyPreferenceItem<Float>()

data class NumberRangeFloatPreferenceItem(
    override val title: String,
    override val summary: String,
    override val prefKey: Preferences.Key<Float>,
    override val singleLineTitle: Boolean = false,
    override val icon: ImageVector? = null,
    override val enabled: Boolean = true,
    override val dependencyKey: Preferences.Key<Boolean>? = null,
    val defaultValue: Float = 0F,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    val steps: Float = 0f,
    val valueRepresentation: (Float) -> String = { it.roundToInt().toString() },
) : KeyPreferenceItem<Float>()

data class SingleListPreferenceItem(
    override val title: String,
    override val summary: String,
    override val prefKey: Preferences.Key<String>,
    override val singleLineTitle: Boolean = false,
    override val icon: ImageVector? = null,
    override val enabled: Boolean = true,
    override val entries: Map<String, String>,
    override val dependencyKey: Preferences.Key<Boolean>? = null,
    val defaultValue: String = "",
) : ListPreferenceItem<String>()

data class MultiListPreferenceItem(
    override val title: String,
    override val summary: String,
    override val prefKey: Preferences.Key<Set<String>>,
    override val singleLineTitle: Boolean = false,
    override val icon: ImageVector? = null,
    override val enabled: Boolean = true,
    override val entries: Map<String, String>,
    override val dependencyKey: Preferences.Key<Boolean>? = null,
    val defaultValue: Set<String> = emptySet(),
) : ListPreferenceItem<Set<String>>()

data class NumberPreferenceItem(
    override val title: String,
    override val summary: String,
    override val prefKey: Preferences.Key<Int>,
    override val singleLineTitle: Boolean = false,
    override val icon: ImageVector? = null,
    override val enabled: Boolean = true,
    override val dependencyKey: Preferences.Key<Boolean>? = null,
    val defaultValue: Int = 0,
    val valueRepresentation: @Composable (Int) -> String,
) : KeyPreferenceItem<Int>()

data class ActionPreferenceItem(
    override val title: String,
    val icon: Painter? = null,
    override val enabled: Boolean = true,
    val action: () -> Unit,
) : BasePreferenceItem
