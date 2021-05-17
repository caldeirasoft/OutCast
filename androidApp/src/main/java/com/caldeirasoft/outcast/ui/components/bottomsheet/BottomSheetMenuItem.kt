package com.caldeirasoft.outcast.ui.components.bottomsheet

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

interface BaseBottomSheetMenuItem

object BottomSheetSeparator : BaseBottomSheetMenuItem

class BottomSheetMenuItem(
    @StringRes val titleId: Int,
    val icon: ImageVector? = null,
    val singleLineTitle: Boolean = false,
    val enabled: Boolean = true,
    val visible: Boolean = true,
    val onClick: () -> Unit = { },
) : BaseBottomSheetMenuItem