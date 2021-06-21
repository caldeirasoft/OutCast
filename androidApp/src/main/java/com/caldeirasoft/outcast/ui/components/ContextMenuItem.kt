package com.caldeirasoft.outcast.ui.components

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

interface BaseContextMenuItem

object ContextMenuSeparator : BaseContextMenuItem

class ContextMenuItem(
    @StringRes val titleId: Int,
    val icon: ImageVector,
    val enabled: Boolean = true,
    val visible: Boolean = true,
    val onClickAction: () -> Unit = { },
) : BaseContextMenuItem