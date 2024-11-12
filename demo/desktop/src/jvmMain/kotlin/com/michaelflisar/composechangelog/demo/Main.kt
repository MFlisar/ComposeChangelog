package com.michaelflisar.composechangelog.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composechangelog.setup
import com.michaelflisar.composechangelog.statesaver.preferences.ChangelogStateSaverPreferences
import com.michaelflisar.composechangelog.statesaver.preferences.create

fun main() {

    application {

        // TODO: get it from the exe in a real jvm application
        val versionName = "1.0.6"

        val setup = ChangelogDefaults.setup(
            versionFormatter = Constants.CHANGELOG_FORMATTER
        )

        Window(
            title = "Changelog Demo",
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(
                position = WindowPosition(Alignment.Center),
                width = 800.dp,
                height = 600.dp
            )
        ) {
            val showChangelog = remember { mutableStateOf(false) }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column {
                    Text("App Version", fontWeight = FontWeight.Bold)
                    Text(
                        "Code: ${Constants.CHANGELOG_FORMATTER.parseVersion(versionName)}",
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        "Name: $versionName",
                        style = MaterialTheme.typography.body1
                    )
                }
                Button(onClick = {
                    showChangelog.value = true
                }) {
                    Text("Show Changelog")
                }
            }

            // manual changelog dialog
            if (showChangelog.value) {
                Changelog.ShowChangelogDialog(setup) {
                    showChangelog.value = false
                }
            }

            // automatic changelog dialog
            val changelogStateSaver = ChangelogStateSaverPreferences.create()
            Changelog.CheckedShowChangelog(changelogStateSaver, versionName, setup)
        }
    }
}