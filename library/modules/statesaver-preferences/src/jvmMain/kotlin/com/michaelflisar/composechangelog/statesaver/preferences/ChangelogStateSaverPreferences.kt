package com.michaelflisar.composechangelog.statesaver.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import java.io.File

fun ChangelogStateSaverPreferences.Companion.create(
    folder: File = File(System.getProperty("user.dir")),
    name: String = "changelog",
    preferenceKey: String = PREF_KEY
): ChangelogStateSaverPreferences {
    return ChangelogStateSaverPreferences(
        dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { File(folder, "$name.preferences_pb").absolutePath.toPath() }
        ),
        preferenceKey = preferenceKey
    )
}