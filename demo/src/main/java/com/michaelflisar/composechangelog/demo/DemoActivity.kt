package com.michaelflisar.composechangelog.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composechangelog.ChangelogUtil
import com.michaelflisar.composechangelog.demo.classes.AppPrefs
import com.michaelflisar.composechangelog.demo.classes.DemoTheme
import com.michaelflisar.composechangelog.demo.composables.MyCollapsibleRegion
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver
import com.michaelflisar.composechangelog.statesaver.kotpreferences.ChangelogStateSaverKotPreferences
import com.michaelflisar.composechangelog.statesaver.preferences.ChangelogStateSaverPreferences
import com.michaelflisar.composepreferences.core.PreferenceScreen
import com.michaelflisar.composepreferences.core.classes.PreferenceSettingsDefaults
import com.michaelflisar.composepreferences.core.classes.PreferenceStyleDefaults
import com.michaelflisar.composechangelog.demo.theme.AppTheme
import com.michaelflisar.composepreferences.kotpreferences.asPreferenceData
import com.michaelflisar.composepreferences.screen.bool.PreferenceBool
import com.michaelflisar.composepreferences.screen.list.PreferenceList
import com.michaelflisar.kotpreferences.compose.collectAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DemoActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // we collect the theme settings (simply via KotPreferences, another library of mine)
            val stateTheme = AppPrefs.theme.collectAsState()
            val stateDynamicTheme = AppPrefs.dynamicTheme.collectAsState()
            val theme = stateTheme.value
            val dynamicTheme = stateDynamicTheme.value
            if (theme == null || dynamicTheme == null)
                return@setContent

            val expandedRootRegions = rememberSaveable(saver = listSaver(
                save = { it.toList() },
                restore = { it.toMutableStateList() }
            )) {
                mutableStateListOf(1, 2)
            }

            AppTheme(
                darkTheme = theme.isDark(),
                dynamicColor = dynamicTheme
            ) {
                // needed - you can also provide your own implementation instead of this simple one
                // (which simply saves the last shown version inside a preference file)
                val changelogStateSaver = ChangelogStateSaverPreferences(LocalContext.current)

                // ALTERNATIVE: if you use my kotpreference library like this demo you can do following:
                val changelogStateSaverKotPrefs =
                    ChangelogStateSaverKotPreferences(AppPrefs.lastShownVersionForChangelog)

                // optional - here you can apply some customisations like changelog resource id, localized texts, styles, filter, sorter, ...
                val setup = ChangelogDefaults.setup()

                // Changelog - this will show the changelog once only if the changelog was not shown for the current app version yet
                Changelog.CheckedShowChangelog(changelogStateSaver, setup)

                // App Main Content
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        TopAppBar(
                            title = { Text(stringResource(R.string.app_name)) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        Content(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            changelogStateSaver,
                            expandedRootRegions
                        )
                    }
                }
            }
        }
    }

    // ----------------
    // UI - Content
    // ----------------

    @Composable
    private fun Content(
        modifier: Modifier,
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

            MyCollapsibleRegion("App Theme", expandedId = 0, expanded = expandedRootRegions) {
                val itemStyle = PreferenceStyleDefaults.item(shape = MaterialTheme.shapes.small)
                val settings = PreferenceSettingsDefaults.settings(
                    itemStyle = itemStyle,
                    animationSpec = null,
                    toggleBooleanOnItemClick = true
                )
                PreferenceScreen(
                    scrollable = false,
                    settings = settings
                ) {
                    PreferenceBool(
                        style = PreferenceBool.Style.Switch,
                        data = AppPrefs.dynamicTheme.asPreferenceData(),
                        title = { Text("Dynamic Theme") },
                        subtitle = { Text("Enable dynamic theme?") },
                        icon = { Icon(Icons.Default.Settings, null) }
                    )
                    PreferenceList(
                        style = PreferenceList.Style.Spinner,
                        data = AppPrefs.theme.asPreferenceData(),
                        items = DemoTheme.values().toList(),
                        itemTextProvider = { it.name },
                        title = { Text("Theme") },
                        icon = { Icon(Icons.Default.ColorLens, null) }
                    )
                }
            }

            val filterDogs = remember { mutableStateOf(false) }
            val useShowMoreButtons = remember { mutableStateOf(true) }
            val useCustomRenderer = remember { mutableStateOf(false) }

            MyCollapsibleRegion("Demo", expandedId = 1, expanded = expandedRootRegions) {
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

            MyCollapsibleRegion("Infos", expandedId = 2, expanded = expandedRootRegions) {
                LazyColumn {
                    infos.forEach {
                        item {
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
                                        Text(item.date, style = MaterialTheme.typography.labelMedium)
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