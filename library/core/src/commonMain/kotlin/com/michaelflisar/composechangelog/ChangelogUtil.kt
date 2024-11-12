package com.michaelflisar.composechangelog

import com.michaelflisar.composechangelog.classes.ShowChangelog
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver


object ChangelogUtil {

    /**
     * checks if changelog should be shown by creating a [ShowChangelog]
     *
     * if last shown version is not the first app install AND if last version is less than current
     * then this will return a [ShowChangelog.Yes] (including the last shown version) to indicate that all changelog entries after the
     * last shown one need to be presented to the user
     *
     * @see [ShowChangelog]
     *
     * @return [ShowChangelog]
     */
    suspend fun shouldShowChangelogOnStart(
        storage: IChangelogStateSaver,
        versionName: String,
        versionFormatter: ChangelogVersionFormatter
    ): ShowChangelog {
        val lastChangelog = storage.lastShownVersion()
        val currentVersion = versionFormatter.parseVersion(versionName)
        return ShowChangelog(lastChangelog, currentVersion.toLong())
    }
}