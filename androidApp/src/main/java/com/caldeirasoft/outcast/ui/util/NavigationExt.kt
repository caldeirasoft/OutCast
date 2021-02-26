package com.caldeirasoft.outcast.ui.util

import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy

inline fun <reified Args : NavArgs> navArgs(savedStateHandle: SavedStateHandle) =
    NavArgsLazy(Args::class) { savedStateHandle.toBundle() }

/** Convert `SavedStateHandle` to `Bundle` */
fun SavedStateHandle.toBundle() =
    bundleOf(*this.keys().map { Pair(it, this.get<Any?>(it)) }.toTypedArray())