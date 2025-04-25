package com.michaelflisar.composechangelog.statesaver.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ChangelogStateSaverPreferences internal constructor(
    private val dataStore: DataStore<Preferences>,
    private val preferenceKey: String,
) : IChangelogStateSaver {

    companion object {
        const val PREF_KEY = "changelogVersion"
    }

    @Composable
    override fun collectLastShownVersion() : State<Long> = dataStore.data
        .map { it[longPreferencesKey(preferenceKey)] ?: -1L }
        .collectAsState(-1L)

    override suspend fun lastShownVersion(): Long {
        return dataStore.data.firstOrNull()?.let {
            it[longPreferencesKey(preferenceKey)]
        } ?: -1L
    }

    override suspend fun saveLastShownVersion(version: Long) {
        withContext(Dispatchers.IO) {}
        dataStore.edit {
            it[longPreferencesKey(preferenceKey)] = version
        }
    }
}