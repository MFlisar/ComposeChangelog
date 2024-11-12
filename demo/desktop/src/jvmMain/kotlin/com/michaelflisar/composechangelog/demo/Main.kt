package com.michaelflisar.composechangelog.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composechangelog.ChangelogUtil
import com.michaelflisar.composechangelog.ShowChangelogDialog
import com.michaelflisar.composechangelog.ShowChangelogDialogIfNecessary
import com.michaelflisar.composechangelog.getAppVersionName
import com.michaelflisar.composechangelog.setup
import com.michaelflisar.composechangelog.statesaver.preferences.ChangelogStateSaverPreferences
import com.michaelflisar.composechangelog.statesaver.preferences.create

fun main() {

    application {

        // does only work if you build an exe file!!! not when you run the debug config...
        // test it with createDistributable for the demo => then you can remove the "if (versionName == "<UNKNOWN>")"!
        var versionName = ChangelogUtil.getAppVersionName()
        println("versionName = $versionName")
        if (versionName == "<UNKNOWN>") {
            versionName = "1.0.5"
        }

        val setup = ChangelogDefaults.setup(
            versionFormatter = Constants.CHANGELOG_FORMATTER
        )

        // saver for the automatic changelog showing
        val changelogStateSaver = ChangelogStateSaverPreferences.create()

        var lastChangelog by remember { mutableStateOf(-1L) }
        LaunchedEffect(Unit) {
            lastChangelog = changelogStateSaver.lastShownVersion()
        }

        MaterialTheme(
            colorScheme = darkColorScheme()
        ) {
            Window(
                title = "Changelog Demo ($versionName)",
                onCloseRequest = ::exitApplication,
                state = rememberWindowState(
                    position = WindowPosition(Alignment.Center),
                    width = 800.dp,
                    height = 600.dp
                )
            ) {
                val showChangelog = remember { mutableStateOf(false) }
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column {
                            Text("App Version", fontWeight = FontWeight.Bold)
                            Text(
                                "Code: ${Constants.CHANGELOG_FORMATTER.parseVersion(versionName)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Name: $versionName",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Last Changelog: $lastChangelog",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Button(onClick = {
                            showChangelog.value = true
                        }) {
                            Text("Show Changelog")
                        }
                    }
                }

                // manual changelog dialog
                if (showChangelog.value) {
                    ShowChangelogDialog(setup) {
                        showChangelog.value = false
                    }
                }

                // automatic changelog dialog
                ShowChangelogDialogIfNecessary(changelogStateSaver, versionName, setup)
            }
        }
    }
}