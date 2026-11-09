package com.michaelflisar.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composechangelog.demo.BuildKonfig
import com.michaelflisar.composechangelog.statesaver.preferences.ChangelogStateSaverPreferences
import com.michaelflisar.composechangelog.statesaver.preferences.create

fun main() {

    Shared.registerHeaderRenderer { group, name ->
        JavaUtil.getMaterialIconFromName(group, name)
    }

    val setup = ChangelogDefaults.setup(
        logFileReader = { Shared.readChangelog() },
        versionFormatter = Shared.CHANGELOG_FORMATTER,
    )

    val changelogStateSaver = ChangelogStateSaverPreferences.create()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = BuildKonfig.appName
        ) {
            DemoApp(
                platform = "Windows",
                setup = setup,
                changelogStateSaver = changelogStateSaver
            )
        }
    }
}