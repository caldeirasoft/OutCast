package com.caldeirasoft.outcast.ui.components.preferences

import androidx.datastore.preferences.core.Preferences

interface PreferenceViewModel {
    fun <T> updatePreference(key: Preferences.Key<T>, value: T)
}