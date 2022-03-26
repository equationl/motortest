package com.equationl.motortest.constants

import androidx.datastore.preferences.core.booleanPreferencesKey

object DataStoreKey {
    val usingCustomPredefined = booleanPreferencesKey("usingCustomPredefined")
    val usingHighAccuracy = booleanPreferencesKey("usingHighAccuracy")
    val runBackground = booleanPreferencesKey("runBackground")
}