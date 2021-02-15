@file:OptIn(ExperimentalAnimationApi::class)
package com.caldeirasoft.outcast.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.ui.util.ComposableFn


val AmbientBottomDrawerContent = compositionLocalOf<BottomDrawerContentState>()
@ExperimentalMaterialApi
val AmbientBottomDrawerState = compositionLocalOf<ModalBottomSheetState>()

@ExperimentalMaterialApi
@Composable
fun CustomBottomDrawerHost(content: @Composable () -> Unit)
{
    val bottomDrawerContent = remember { BottomDrawerContentState() }
    val sheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden) { newState ->
            when (newState) {
                ModalBottomSheetValue.Hidden -> Unit
                    //bottomDrawerContent.updateContent(emptyContent())
                ModalBottomSheetValue.HalfExpanded -> Unit
                ModalBottomSheetValue.Expanded -> Unit
            }
            true
        }

    Providers(
        AmbientBottomDrawerContent provides bottomDrawerContent,
        AmbientBottomDrawerState provides sheetState)
    {
        Log_D("TAG", "drawerState: ${sheetState.isVisible}")
        ModalBottomSheetLayout(
            sheetContent = { bottomDrawerContent.content.value() },
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp),
        ) {
            content()
        }
    }
}

open class BottomDrawerContent(
    val content: ComposableFn
) {
    object Empty : BottomDrawerContent({ })
}

class BottomDrawerContentState {
    /**
     * Bottom Drawer content
     */
    var content: MutableState<ComposableFn> = mutableStateOf({ })
        private set

    /**
     * Update bottom drawer content
     */
    fun updateContent(newContent: ComposableFn) {
        content.value = newContent
    }
}
