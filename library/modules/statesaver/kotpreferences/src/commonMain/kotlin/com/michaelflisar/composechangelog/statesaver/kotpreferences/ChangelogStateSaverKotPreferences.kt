package com.michaelflisar.composechangelog.statesaver.kotpreferences

import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver
import com.michaelflisar.kotpreferences.compose.collectAsStateNotNull
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

class ChangelogStateSaverKotPreferences(
    private val setting: StorageSetting<Long>
) : IChangelogStateSaver {

    @Composable
    override fun collectLastShownVersion() : State<Long> = setting.collectAsStateNotNull()

    override suspend fun lastShownVersion(): Long {
        return setting.read()
    }

    override suspend fun saveLastShownVersion(version: Long) {
        setting.update(version)
    }
}