package com.michaelflisar.composechangelog.demo.classes

import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.datastore.DataStoreStorage

object AppPrefs : SettingsModel(DataStoreStorage(name = "app_prefs")) {
    val theme by enumPref(DemoTheme.System)
    val dynamicTheme by boolPref(false)

    val lastShownVersionForChangelog by longPref(-1L)
}