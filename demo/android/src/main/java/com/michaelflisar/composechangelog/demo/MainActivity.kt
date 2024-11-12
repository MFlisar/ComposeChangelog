package com.michaelflisar.composechangelog.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composechangelog.ChangelogUtil
import com.michaelflisar.composechangelog.ShowChangelogDialog
import com.michaelflisar.composechangelog.ShowChangelogDialogIfNecessary
import com.michaelflisar.composechangelog.demo.classes.DemoPrefs
import com.michaelflisar.composechangelog.getAppVersionCode
import com.michaelflisar.composechangelog.getAppVersionName
import com.michaelflisar.composechangelog.setup
import com.michaelflisar.composechangelog.statesaver.kotpreferences.ChangelogStateSaverKotPreferences
import com.michaelflisar.composechangelog.statesaver.preferences.ChangelogStateSaverPreferences
import com.michaelflisar.composechangelog.statesaver.preferences.create
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.toolbox.androiddemoapp.DemoActivity
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoAppThemeRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoCollapsibleRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.rememberDemoExpandedRegions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : DemoActivity() {

    @Composable
    override fun ColumnScope.Content(
        themeState: ComposeTheme.State
    ) {
        val context = LocalContext.current

        val regionState = rememberDemoExpandedRegions(listOf(1, 2))

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
            versionFormatter = Constants.CHANGELOG_FORMATTER
        )

        // Changelog - this will show the changelog once only if the changelog was not shown for the current app version yet
        val versionName = ChangelogUtil.getAppVersionName(context)
        ShowChangelogDialogIfNecessary(changelogStateSaver, versionName, setup)

        val showChangelog = remember { mutableStateOf(false) }
        val infos = remember { mutableStateListOf<String>() }
        val filterDogs = remember { mutableStateOf(false) }
        val useShowMoreButtons = remember { mutableStateOf(true) }
        val useCustomRenderer = remember { mutableStateOf(false) }

        // Content
        Content(
            regionState,
            showChangelog,
            changelogStateSaver,
            infos,
            filterDogs,
            useShowMoreButtons,
            useCustomRenderer
        )

        // eventually show a full changelog dialog
        if (showChangelog.value) {
            val setup = ChangelogDefaults.setup(
                context = context,
                filter = if (filterDogs.value) ChangelogDefaults.filter(
                    "dogs",
                    false
                ) else null,
                useShowMoreButtons = useShowMoreButtons.value,
                renderer = if (useCustomRenderer.value) {
                    // we only adjust the release item renderer, but of course you can do whatever you want here
                    ChangelogDefaults.renderer(
                        itemRelease = { modifier, item, setup ->
                            Card(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        item.versionInfo,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        item.date,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                        },
                        item = { modifier, item, setup ->
                            ChangelogDefaults.defaultItem(item, tagAlignment = Alignment.Start)
                        }
                    )
                } else ChangelogDefaults.renderer(),
                versionFormatter = Constants.CHANGELOG_FORMATTER
            )
            ShowChangelogDialog(setup) {
                showChangelog.value = false
            }
        }

        LaunchedEffect(showChangelog) {
            infos.add("showChangelog = $showChangelog")
        }
    }

    @Composable
    private fun Content(
        regionState: DemoCollapsibleRegion.State,
        showChangelog: MutableState<Boolean>,
        changelogStateSaver: ChangelogStateSaverPreferences,
        infos: SnapshotStateList<String>,
        filterDogs: MutableState<Boolean>,
        useShowMoreButtons: MutableState<Boolean>,
        useCustomRenderer: MutableState<Boolean>
    ) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        // Infos about demo app
        Column {
            Text("App Version", fontWeight = FontWeight.Bold)
            Text(
                "Code: ${ChangelogUtil.getAppVersionCode(context)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Name: ${ChangelogUtil.getAppVersionName(context)}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // App Theme
        DemoAppThemeRegion(0, regionState)

        // Demo
        DemoCollapsibleRegion("Demo", regionId = 1, state = regionState) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show dog changelogs only?", modifier = Modifier.weight(1f))
                Checkbox(checked = filterDogs.value, onCheckedChange = {
                    filterDogs.value = it
                })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Use show more buttons?", modifier = Modifier.weight(1f))
                Checkbox(checked = useShowMoreButtons.value, onCheckedChange = {
                    useShowMoreButtons.value = it
                })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Custom renderer?", modifier = Modifier.weight(1f))
                Checkbox(checked = useCustomRenderer.value, onCheckedChange = {
                    useCustomRenderer.value = it
                })
            }

            OutlinedButton(
                onClick = { showChangelog.value = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Open Changelog")
            }
            OutlinedButton(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        changelogStateSaver.saveLastShownVersion(0L)
                    }
                    infos.add("changelog - last shown version resettet to 0")
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset Last Shown Version")
            }
            OutlinedButton(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        val versionName = ChangelogUtil.getAppVersionName(context)
                        val showChangelog =
                            ChangelogUtil.shouldShowChangelogOnStart(
                                changelogStateSaver,
                                versionName,
                                Constants.CHANGELOG_FORMATTER
                            )
                        infos.add("shouldShow = ${showChangelog.shouldShow} ($showChangelog)")
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Check if changelog should be shown")
            }
        }

        // Infos
        DemoCollapsibleRegion("Infos", regionId = 2, state = regionState) {
            Column {
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