package com.caldeirasoft.outcast.domain.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
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
    abstract val dependencyValue: Boolean?
}

class EmptyPreferenceItem(
    override val title: String,
    override val summary: String,
    override val icon: ImageVector? = null,
    override val singleLineTitle: Boolean = false,
    override val enabled: Boolean = true,
    override val dependencyValue: Boolean? = null,
    val onClick: () -> Unit = { },
) : PreferenceItem()

sealed class KeyPreferenceItem<T> : PreferenceItem() {
    abstract val prefKey: String
}

sealed class ListPreferenceItem<T> : KeyPreferenceItem<T>() {
    abstract val entries: Map<T, String>
}

data class SwitchPreferenceItem(
    override val title: String,
    override val summary: String,
    override val prefKey: String,
    override val icon: ImageVector? = null,
    override val singleLineTitle: Boolean = false,
    override val enabled: Boolean = true,
    override val dependencyValue: Boolean? = null,
    val defaultValue: Boolean = false,
    val onValueChanged: (Boolean) -> Unit,
) : KeyPreferenceItem<Boolean>()

data class SeekbarFloatPreferenceItem(
    override val title: String,
    override val summary: String,
    override val prefKey: String,
    override val icon: ImageVector? = null,
    override val singleLineTitle: Boolean = false,
    override val enabled: Boolean = true,
    override val dependencyValue: Boolean? = null,
    val defaultValue: Float = 0F,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    val steps: Int = 0,
    val valueRepresentation: (Float) -> String,
    val onValueChanged: (Float) -> Unit,
) : KeyPreferenceItem<Float>()

data class NumberRangeFloatPreferenceItem(
    override val title: String,
    override val summary: String,
    override val prefKey: String,
    override val singleLineTitle: Boolean = false,
    override val icon: ImageVector? = null,
    override val enabled: Boolean = true,
    override val dependencyValue: Boolean? = null,
    val defaultValue: Float = 0F,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    val steps: Float = 0f,
    val valueRepresentation: (Float) -> String = { it.roundToInt().toString() },
    val onValueChanged: (Float) -> Unit,
) : KeyPreferenceItem<Float>()

data class SingleListPreferenceItem<T>(
    override val title: String,
    override val summary: String,
    override val prefKey: String,
    override val singleLineTitle: Boolean = false,
    override val icon: ImageVector? = null,
    override val enabled: Boolean = true,
    override val entries: Map<T, String>,
    override val dependencyValue: Boolean? = null,
    val defaultValue: T,
    val onValueChanged: (T) -> Unit,
) : ListPreferenceItem<T>()

data class MultiListPreferenceItem<T>(
    override val title: String,
    override val summary: String,
    override val prefKey: String,
    override val singleLineTitle: Boolean = false,
    override val icon: ImageVector? = null,
    override val enabled: Boolean = true,
    override val entries: Map<T, String>,
    override val dependencyValue: Boolean? = null,
    val defaultValue: Set<T> = emptySet(),
    val onValueChanged: (Set<T>) -> Unit,
) : ListPreferenceItem<T>()

data class NumberPreferenceItem(
    override val title: String,
    override val summary: String,
    override val prefKey: String,
    override val singleLineTitle: Boolean = false,
    override val icon: ImageVector? = null,
    override val enabled: Boolean = true,
    override val dependencyValue: Boolean? = null,
    val defaultValue: Int = 0,
    val valueRepresentation: @Composable (Int) -> String,
    val onValueChanged: (Int) -> Unit,
) : KeyPreferenceItem<Int>()

data class ActionPreferenceItem(
    override val title: String,
    val icon: Painter? = null,
    override val enabled: Boolean = true,
    val action: () -> Unit,
) : BasePreferenceItem
