package com.caldeirasoft.outcast.ui.components

import androidx.compose.runtime.Composable
import com.caldeirasoft.outcast.ui.util.ScreenState
import java.lang.reflect.Modifier

@Composable
fun LoadingContent(
    screenState: ScreenState,
    modifier: Modifier,
    onLoading: @Composable () -> Unit = { LoadingScreen() },
    onError: @Composable () -> Unit = {},
    onSuccessEmpty: @Composable () -> Unit = {},
    onSuccess: @Composable () -> Unit,
)
{
    screenState.run {
        when (this) {
            is ScreenState.Loading -> onLoading()
            is ScreenState.Error -> onError()
            is ScreenState.Empty -> onSuccessEmpty()
            is ScreenState.Success -> onSuccess()
        }
    }
}