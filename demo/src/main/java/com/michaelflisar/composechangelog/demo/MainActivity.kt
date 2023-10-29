package com.michaelflisar.composechangelog.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composechangelog.ChangelogUtil
import com.michaelflisar.composechangelog.demo.classes.DemoPrefs
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver
import com.michaelflisar.composechangelog.statesaver.kotpreferences.ChangelogStateSaverKotPreferences
import com.michaelflisar.composechangelog.statesaver.preferences.ChangelogStateSaverPreferences
import com.michaelflisar.composedemobaseactivity.DemoBaseActivity
import com.michaelflisar.composedemobaseactivity.classes.DemoTheme
import com.michaelflisar.composedemobaseactivity.classes.listSaverKeepEntryStateList
import com.michaelflisar.composedemobaseactivity.composables.DemoAppThemeRegion
import com.michaelflisar.composedemobaseactivity.composables.DemoCollapsibleRegion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : DemoBaseActivity() {

    @Composable
    override fun Content(modifier: Modifier, theme: DemoTheme, dynamicTheme: Boolean) {

        val expandedRootRegions = rememberSaveable(Unit, saver = listSaverKeepEntryStateList()) {
            mutableStateListOf(1, 2)
        }

        // needed - you can also provide your own implementation instead of this simple one
        // (which simply saves the last shown version inside a preference file)
        val changelogStateSaver = ChangelogStateSaverPreferences(LocalContext.current)

        // ALTERNATIVE: if you use my kotpreference library like this demo you can do following:
        val changelogStateSaverKotPrefs =
            ChangelogStateSaverKotPreferences(DemoPrefs.lastShownVersionForChangelog)

        // optional - here you can apply some customisations like changelog resource id, localized texts, styles, filter, sorter, ...
        val setup = ChangelogDefaults.setup()

        // Changelog - this will show the changelog once only if the changelog was not shown for the current app version yet
        Changelog.CheckedShowChangelog(changelogStateSaver, setup)

        Content(
            modifier = modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            theme,
            dynamicTheme,
            changelogStateSaver,
            expandedRootRegions
        )
    }

    // ----------------
    // UI - Content
    // ----------------

    @Composable
    private fun Content(
        modifier: Modifier,
        theme: DemoTheme,
        dynamicTheme: Boolean,
        changelogStateSaver: IChangelogStateSaver,
        expandedRootRegions: SnapshotStateList<Int>
    ) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        var showChangelog by remember { mutableStateOf(false) }
        val infos = remember { mutableStateListOf<String>() }

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            DemoAppThemeRegion(theme, dynamicTheme, id = 0, expandedIds = expandedRootRegions)

            val filterDogs = remember { mutableStateOf(false) }
            val useShowMoreButtons = remember { mutableStateOf(true) }
            val useCustomRenderer = remember { mutableStateOf(false) }

            DemoCollapsibleRegion("Demo", id = 1, expandedIds = expandedRootRegions) {
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
                    onClick = { showChangelog = true },
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
                            val showChangelog =
                                ChangelogUtil.shouldShowChangelogOnStart(
                                    context,
                                    changelogStateSaver
                                )
                            infos.add("shouldShow = ${showChangelog.shouldShow} ($showChangelog)")
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Check if changelog should be shown")
                }
            }

            DemoCollapsibleRegion("Infos", id = 2, expandedIds = expandedRootRegions) {
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

            // eventually show a full changelog dialog
            if (showChangelog) {
                val setup = ChangelogDefaults.setup(
                    filter = if (filterDogs.value) ChangelogDefaults.filter(
                        "dogs",
                        false
                    ) else null,
                    useShowMoreButtons = useShowMoreButtons.value,
                    renderer = if (useCustomRenderer.value) {
                        // we only adjuut the release item renderer, but of course you can do whatever you want here
                        ChangelogDefaults.DEFAULT_RENDERER.copy(
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
                                            setup.versionCodeFormatter(item.versionCode),
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            item.date,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }

                            }
                        )
                    } else ChangelogDefaults.DEFAULT_RENDERER
                )
                Changelog.ShowChangelogDialog(setup) {
                    showChangelog = false
                }
            }

            LaunchedEffect(showChangelog) {
                infos.add("showChangelog = $showChangelog")
            }
        }
    }
}