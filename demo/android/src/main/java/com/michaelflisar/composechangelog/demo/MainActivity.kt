package com.michaelflisar.composechangelog.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composechangelog.DefaultVersionFormatter
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.ChangelogState
import com.michaelflisar.composechangelog.classes.rememberChangelogState
import com.michaelflisar.composechangelog.demo.classes.DemoPrefs
import com.michaelflisar.composechangelog.getAppVersionCode
import com.michaelflisar.composechangelog.getAppVersionName
import com.michaelflisar.composechangelog.rememberChangelogData
import com.michaelflisar.composechangelog.setup
import com.michaelflisar.composechangelog.statesaver.kotpreferences.ChangelogStateSaverKotPreferences
import com.michaelflisar.composechangelog.statesaver.preferences.ChangelogStateSaverPreferences
import com.michaelflisar.composechangelog.statesaver.preferences.create
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.androiddemoapp.DemoActivity
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoAppThemeRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoCollapsibleRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.rememberDemoExpandedRegions
import com.michaelflisar.toolbox.composables.MyColumn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val CHANGELOG_FORMATTER =
    DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatchCandidate)

class MainActivity : DemoActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ColumnScope.Content(
        themeState: ComposeTheme.State,
    ) {
        val context = LocalContext.current

        val regionState = rememberDemoExpandedRegions(ids = listOf(1, 2))

        // needed - you can also provide your own implementation instead of this simple one
        // (which simply saves the last shown version inside a preference file)
        val changelogStateSaver = remember {
            ChangelogStateSaverPreferences.create(context)
        }

        // ALTERNATIVE: if you use my kotpreference library like this demo you can do following:
        val changelogStateSaverKotPrefs = remember {
            ChangelogStateSaverKotPreferences(DemoPrefs.lastShownVersionForChangelog)
        }

        // optional - here you can apply some customisations like changelog resource id, localized texts, styles, filter, sorter, ...
        val setup = ChangelogDefaults.setup(
            context = context,
            //changelogResourceId = R.raw.changelog, // this is the default
            versionFormatter = CHANGELOG_FORMATTER
        )

        // Changelog - this will show the changelog once only if the changelog was not shown for the current app version yet
        val versionName = Changelog.getAppVersionName(context)

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

        val infos = remember { mutableStateListOf<String>() }

        // Content
        Content(
            regionState,
            changelogState,
            changelogStateSaver,
            infos
        )

        // eventually show a full changelog dialog
        if (changelogState.visible) {
            ModalBottomSheet(
                onDismissRequest = {
                    changelogState.hide()
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "Changelog",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Changelog(changelogState, setup, Modifier.fillMaxWidth())
                }
            }
        }

        LaunchedEffect(changelogState.visible) {
            infos.add("showChangelog.visible = ${changelogState.visible}")
        }
    }

    @Composable
    private fun Content(
        regionState: DemoCollapsibleRegion.State,
        showChangelog: ChangelogState,
        changelogStateSaver: ChangelogStateSaverPreferences,
        infos: SnapshotStateList<String>,
    ) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        // Infos about demo app
        Column {
            Text("App Version", fontWeight = FontWeight.Bold)
            Text(
                "Code: ${Changelog.getAppVersionCode(context)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Name: ${Changelog.getAppVersionName(context)}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // App Theme
        DemoAppThemeRegion(0, regionState)

        // Demo
        DemoCollapsibleRegion("Demo", regionId = 1, state = regionState) {
            MyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { showChangelog.show() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Open Changelog")
                }
                OutlinedButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            changelogStateSaver.saveLastShownVersion(0L)
                        }

                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Reset Last Shown Version")
                }
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            changelogStateSaver.saveLastShownVersion(
                                CHANGELOG_FORMATTER.parseVersion(
                                    "1.0.0"
                                ).toLong()
                            )
                        }
                        infos.add("changelog - last shown version resettet to 1.0.0")
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Reset Last Changelog to 1.0.0")
                }
                OutlinedButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val versionName = Changelog.getAppVersionName(context)
                            scope.launch {
                                showChangelog.checkShouldShowChangelogOnStart(
                                    changelogStateSaver,
                                    versionName,
                                    CHANGELOG_FORMATTER
                                )
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Check if changelog should be shown")
                }
            }
        }

        // Infos
        DemoCollapsibleRegion("Infos", regionId = 2, state = regionState) {
            MyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                infos.forEach {
                    Row {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(Icons.Default.Circle, null, modifier = Modifier.size(8.dp))
                            // simple hack to align circle with the first line of the text
                            Text(" ", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(it, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}