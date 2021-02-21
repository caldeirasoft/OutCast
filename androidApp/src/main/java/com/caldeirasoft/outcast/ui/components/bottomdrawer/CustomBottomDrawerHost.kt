package com.caldeirasoft.outcast.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.ui.components.bottomdrawer.CustomBottomDrawerState
import com.caldeirasoft.outcast.ui.components.bottomdrawer.CustomBottomDrawerValue
import com.caldeirasoft.outcast.ui.components.bottomdrawer.rememberBottomDrawerState
import com.caldeirasoft.outcast.ui.util.ComposableFn


val AmbientBottomDrawerContent = ambientOf<BottomDrawerContentState>()
val AmbientBottomDrawerState = ambientOf<CustomBottomDrawerState>()

@ExperimentalMaterialApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CustomBottomDrawerHost(content: @Composable () -> Unit)
{
    val bottomDrawerContent = remember { BottomDrawerContentState() }
    val drawerState =
        rememberBottomDrawerState(initialValue = CustomBottomDrawerValue.Closed) { newState ->
            when (newState) {
                CustomBottomDrawerValue.Closed -> Unit
                    //bottomDrawerContent.updateContent(emptyContent())
                CustomBottomDrawerValue.Open -> Unit
                CustomBottomDrawerValue.Expanded -> Unit
            }
            true
        }

    Providers(
        AmbientBottomDrawerContent provides bottomDrawerContent,
        AmbientBottomDrawerState provides drawerState)
    {
        Log_D("TAG", "drawerState: ${drawerState.isClosed}")
        content()
    }
}

open class BottomDrawerContent(
    val content: ComposableFn
) {
    object Empty : BottomDrawerContent(emptyContent())
}

class BottomDrawerContentState {
    /**
     * Bottom Drawer content
     */
    var content: MutableState<ComposableFn> = mutableStateOf(emptyContent())
        private set

    /**
     * Update bottom drawer content
     */
    fun updateContent(newContent: ComposableFn) {
        content.value = newContent
    }
}
