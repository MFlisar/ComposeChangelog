package com.michaelflisar.composechangelog.demo.classes

import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.datastore.DataStoreStorage

object DemoPrefs : SettingsModel(DataStoreStorage(name = "demo_prefs")) {
    val lastShownVersionForChangelog by longPref(-1L)
}