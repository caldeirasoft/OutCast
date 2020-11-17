package com.caldeirasoft.outcast.ui.util

sealed class ScreenState {
    object Idle : ScreenState()
    object Loading : ScreenState()
    data class Error(val t: Throwable) : ScreenState()
    object Success : ScreenState()
    object Empty : ScreenState()
}