package com.equationl.motortest

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.settingDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class App: Application() {
}