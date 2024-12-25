package com.michaelflisar.composechangelog.statesaver.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull

class ChangelogStateSaverPreferences internal constructor(
    private val dataStore: DataStore<Preferences>,
    private val preferenceKey: String
) : IChangelogStateSaver {

    companion object {
        const val PREF_KEY = "changelogVersion"
    }

    override suspend fun lastShownVersion(): Long {
        return dataStore.data.firstOrNull()?.let {
            it[longPreferencesKey(preferenceKey)]
        } ?: -1L
    }

    override suspend fun saveLastShownVersion(version: Long) {
        dataStore.edit {
            it[longPreferencesKey(preferenceKey)] = version
        }
    }
}