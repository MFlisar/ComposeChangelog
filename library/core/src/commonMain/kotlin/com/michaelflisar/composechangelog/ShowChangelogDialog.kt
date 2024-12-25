package com.michaelflisar.composechangelog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItem
import com.michaelflisar.composechangelog.classes.DataItemRelease
import com.michaelflisar.composechangelog.classes.ShowChangelog
import com.michaelflisar.composechangelog.interfaces.IChangelogFilter
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ShowChangelogDialog(
    setup: ChangelogSetup,
    onDismiss: () -> Unit = {}
) {
    val changelog = remember { mutableStateOf<ChangelogData?>(null) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val data = ChangelogUtil.readFile(setup.logFileReader, setup.versionFormatter)
            changelog.value = filterAndSort(data, setup)
        }
    }

    val data = changelog.value
    if (data != null && !data.isEmpty()) {
        ShowChangelogDialog(
            data = data,
            setup = setup,
            onDismiss = {
                onDismiss()
            }
        )
    }
}

@Composable
fun ShowChangelogDialogIfNecessary(
    stateSaver: IChangelogStateSaver,
    versionName: String,
    setup: ChangelogSetup
) {
    val scope = rememberCoroutineScope()
    var showChangelog by rememberSaveable(setup) { mutableStateOf<ShowChangelog?>(null) }
    LaunchedEffect(setup) {
        if (showChangelog == null) {
            showChangelog = ChangelogUtil.shouldShowChangelogOnStart(
                stateSaver,
                versionName,
                setup.versionFormatter
            )
            if (showChangelog?.isInitialVersion == true) {
                //println("Updating last shown: ${showChangelog!!.currentVersion}")
                scope.launch(Dispatchers.IO) {
                    stateSaver.saveLastShownVersion(showChangelog!!.currentVersion)
                }
            }
        }
    }

    showChangelog
        ?.takeIf { it.shouldShow }
        ?.let { data ->
            val filter = object : IChangelogFilter {
                override fun keep(release: DataItemRelease): Boolean {
                    return release.versionCode > data.lastShownVersion && (setup.filter?.keep(
                        release
                    ) ?: true)
                }

                override fun keep(release: DataItemRelease, item: DataItem): Boolean {
                    return setup.filter?.keep(release) ?: true
                }
            }
            val setup = setup.copy(
                filter = filter
            )
            ShowChangelogDialog(setup) {
                scope.launch(Dispatchers.IO) {
                    stateSaver.saveLastShownVersion(data.currentVersion)
                    showChangelog = ShowChangelog(data.currentVersion, data.currentVersion)
                }
            }
        }
}

private fun filterAndSort(
    changelog: ChangelogData,
    setup: ChangelogSetup
): ChangelogData {
    return ChangelogData(
        releases = changelog.releases
            .filter { setup.filter?.keep(it) ?: true }
            .map { release ->
                val filtered = setup.filter?.let { filter ->
                    release.items.filter {
                        filter.keep(
                            release,
                            it
                        )
                    }
                } ?: release.items
                val sorted = setup.sorter?.let { it(filtered) } ?: filtered
                release.copy(items = sorted)
            }
            .filter { it.items.isNotEmpty() }
    )
}