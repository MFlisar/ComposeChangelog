package com.michaelflisar.composechangelog.statesaver.kotpreferences

import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting

class ChangelogStateSaverKotPreferences(
    private val setting: StorageSetting<Long>
) : IChangelogStateSaver {

    override suspend fun lastShownVersion(): Long {
        return setting.read()
    }

    override suspend fun saveLastShownVersion(version: Long) {
        setting.update(version)
    }
}