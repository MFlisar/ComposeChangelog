package com.michaelflisar.composechangelog.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composechangelog.DefaultVersionFormatter
import com.michaelflisar.composechangelog.classes.rememberChangelogState
import com.michaelflisar.composechangelog.getAppVersionName
import com.michaelflisar.composechangelog.renderer.header.ChangelogHeaderRenderer
import com.michaelflisar.composechangelog.setup
import com.michaelflisar.composechangelog.statesaver.preferences.ChangelogStateSaverPreferences
import com.michaelflisar.composechangelog.statesaver.preferences.create
import kotlinx.coroutines.launch

val CHANGELOG_FORMATTER =
    DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatchCandidate)

// helper function to get icons from androidx.compose.material.icons.*
// using reflection....
fun getMaterialIconFromName(group: String, name: String): ImageVector? {
    val cls = when (group) {
        "",
        "Filled" -> Icons.Filled
        "Outlined" -> Icons.Outlined
        "Sharp" -> Icons.Sharp
        "Rounded" -> Icons.Rounded
        "TwoTone" -> Icons.TwoTone
        else -> throw RuntimeException("Unknown group: $group")
    }

    val grp = cls.javaClass.simpleName.lowercase()
    val className = "androidx.compose.material.icons.$grp.${name}Kt"

    val cl = Class.forName(className)
    val method = cl.declaredMethods.first()
    return method.invoke(null, cls) as ImageVector
}

fun main() {

    // register custom renderers
    Changelog.registerRenderer(
        ChangelogHeaderRenderer {

            val parts = it?.split(".") ?: return@ChangelogHeaderRenderer
            if (parts.isEmpty()) return@ChangelogHeaderRenderer

            val group = if (parts.size == 2) parts[0] else ""
            val name = if (parts.size == 1) parts[0] else parts[1]

            val icon = try {
                getMaterialIconFromName(group, name)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }

        }
    )

    application {

        // does only work if you build an exe file!!! not when you run the debug config...
        // test it with createDistributable for the demo => then you can remove the "if (versionName == "<UNKNOWN>")"!
        var versionName = Changelog.getAppVersionName()
        println("versionName = $versionName")
        if (versionName == "<UNKNOWN>") {
            versionName = "1.0.5"
        }

        val setup = ChangelogDefaults.setup(
            versionFormatter = CHANGELOG_FORMATTER,
            // optional - to support html tags in the changelog
            // by default the desktop implementation does remove html tags so that you still can use
            // use them in a common project and do not need to care about it
            // => AnnotatedString.fromHtml(this) does only work on android...
            // => here I show you how to use it with a 3rd party library
            textFormatter = { remember(it) { htmlToAnnotatedString(it) } }
        )

        // saver for the automatic changelog showing
        val changelogStateSaver = ChangelogStateSaverPreferences.create()
        val lastChangelog by changelogStateSaver.collectLastShownVersion()

        MaterialTheme(
            colorScheme = lightColorScheme()
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
                val scope = rememberCoroutineScope()

                val changelogState = rememberChangelogState()
                // initially we check if we need to show the changelog
                // this is optional of course...
                LaunchedEffect(Unit) {
                    changelogState.checkShouldShowChangelogOnStart(
                        changelogStateSaver,
                        versionName,
                        CHANGELOG_FORMATTER
                    )
                }

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
                                "Code: ${CHANGELOG_FORMATTER.parseVersion(versionName)}",
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
                            changelogState.show()
                        }) {
                            Text("Show Changelog")

                        }
                        Button(onClick = {
                            scope.launch {
                                changelogStateSaver.saveLastShownVersion(
                                    CHANGELOG_FORMATTER.parseVersion(
                                        "1.0.0"
                                    ).toLong()
                                )
                            }
                        }) {
                            Text("Reset Last Changelog to 1.0.0")
                        }
                        Button(onClick = {
                            scope.launch {
                                changelogState.checkShouldShowChangelogOnStart(
                                    changelogStateSaver,
                                    versionName,
                                    CHANGELOG_FORMATTER
                                )
                            }
                        }) {
                            Text("Check if changelog should be shown")
                        }
                    }
                }

                // show changelog dialog
                if (changelogState.visible) {
                    Dialog(
                        visible = true,
                        title = "Changelog",
                        onCloseRequest = { changelogState.hide() }
                    ) {
                        Changelog(changelogState, setup, Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}