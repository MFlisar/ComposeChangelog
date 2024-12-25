package com.michaelflisar.composechangelog.statesaver.preferences

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath

fun ChangelogStateSaverPreferences.Companion.create(
    context: Context,
    name: String = "changelog",
    preferenceKey: String = PREF_KEY
): ChangelogStateSaverPreferences {
    return ChangelogStateSaverPreferences(
        dataStore = PreferenceDataStoreFactory.createWithPath(produceFile = {
            context.filesDir.resolve("$name.preferences_pb").absolutePath.toPath()
        }),
        preferenceKey = preferenceKey
    )
}