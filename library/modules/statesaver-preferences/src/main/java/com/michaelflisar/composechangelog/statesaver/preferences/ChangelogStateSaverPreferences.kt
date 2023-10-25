package com.michaelflisar.composechangelog.statesaver.preferences

import android.content.Context
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver

class ChangelogStateSaverPreferences(
    context: Context,
    private val preferenceFileName: String = PREF_FILE,
    private val preferenceKey: String = PREF_KEY
) : IChangelogStateSaver {

    private val prefs by lazy {
        context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)
    }

    companion object {
        private const val PREF_FILE = "com.michaelflisar.changelog"
        private const val PREF_KEY = "changelogVersion"
    }

    override suspend fun lastShownVersion(): Long {
        return prefs.getLong(preferenceKey, -1L)
    }

    override suspend fun saveLastShownVersion(version: Long) {
        prefs.edit().putLong(preferenceKey, version).apply()
    }
}