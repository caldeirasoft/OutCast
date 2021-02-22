package com.caldeirasoft.outcast.ui.components.bottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.util.Log_D


val LocalBottomSheetContent = compositionLocalOf<ModalBottomSheetContent>()
@ExperimentalMaterialApi
val LocalBottomSheetState = compositionLocalOf<ModalBottomSheetState>()

@ExperimentalMaterialApi
@Composable
fun ModalBottomSheetHost(content: @Composable () -> Unit)
{
    val bottomSheetContent = remember {
        ModalBottomSheetContent {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
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
        LocalBottomSheetContent provides bottomSheetContent,
        LocalBottomSheetState provides sheetState)
    {
        Log_D("TAG", "drawerState: ${sheetState.isVisible}")
        ModalBottomSheetLayout(
            sheetContent = { bottomSheetContent.content.value() },
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
