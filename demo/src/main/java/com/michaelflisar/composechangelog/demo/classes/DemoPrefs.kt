package com.michaelflisar.composechangelog.demo.classes

import com.michaelflisar.kotpreferences.core.SettingsModel
import com.michaelflisar.kotpreferences.storage.datastore.DataStoreStorage
import com.michaelflisar.kotpreferences.storage.datastore.create

object DemoPrefs : SettingsModel(DataStoreStorage.create(name = "changelog_demo_prefs")) {
    val lastShownVersionForChangelog by longPref(-1L)
}