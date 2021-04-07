// PluralResources.kt

package com.caldeirasoft.outcast.ui.components.foundation

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

@Composable
fun quantityStringResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String {
    return LocalContext.current.resources.getQuantityString(id, quantity, *formatArgs)
}

@Composable
fun quantityStringResourceZero(
    @PluralsRes id: Int,
    @StringRes zeroResId: Int,
    quantity: Int,
    vararg formatArgs: Any,
): String {
    return if (quantity == 0)
        stringResource(id = zeroResId)
    else quantityStringResource(id = id, quantity = quantity, formatArgs = formatArgs)
}