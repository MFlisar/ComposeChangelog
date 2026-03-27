package com.michaelflisar.demo

import android.app.Application
import android.content.Context
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composechangelog.statesaver.preferences.ChangelogStateSaverPreferences
import com.michaelflisar.composechangelog.statesaver.preferences.create

class App : Application() {

    companion object {

        internal var changelogStateSaver: ChangelogStateSaverPreferences? = null

        fun ChangelogStateSaver(context: Context): ChangelogStateSaverPreferences {
            if (changelogStateSaver == null)
                changelogStateSaver = ChangelogStateSaverPreferences.create(context.applicationContext)
            return changelogStateSaver!!
        }

        val ChangelogSetup = ChangelogDefaults.setup(
            logFileReader = { Shared.readChangelog() },
            versionFormatter = Shared.CHANGELOG_FORMATTER,
        )
    }

    override fun onCreate() {
        super.onCreate()

        Shared.registerHeaderRenderer { group, name ->
            JavaUtil.getMaterialIconFromName(group, name)
        }

    }
}