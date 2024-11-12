package com.michaelflisar.composechangelog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItem
import com.michaelflisar.composechangelog.classes.DataItemRelease
import com.michaelflisar.composechangelog.classes.ShowChangelog
import com.michaelflisar.composechangelog.composables.Changelog
import com.michaelflisar.composechangelog.composables.ChangelogItem
import com.michaelflisar.composechangelog.composables.ChangelogItemMore
import com.michaelflisar.composechangelog.composables.ChangelogItemRelease
import com.michaelflisar.composechangelog.composables.ChangelogTag
import com.michaelflisar.composechangelog.interfaces.IChangelogFilter
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver
import composechangelog.composechangelog.core.generated.resources.Res
import composechangelog.composechangelog.core.generated.resources.changelog_dialog_title
import composechangelog.composechangelog.core.generated.resources.changelog_button_show_more
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource

object Changelog {

    fun filterAndSort(
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

    @Composable
    fun CheckedShowChangelog(
        storage: IChangelogStateSaver,
        versionName: String,
        setup: ChangelogSetup
    ) {
        val scope = rememberCoroutineScope()
        var showChangelog by rememberSaveable(setup) { mutableStateOf<ShowChangelog?>(null) }
        LaunchedEffect(setup) {
            println("showChangelog1 = $showChangelog")
            if (showChangelog == null) {
                println("showChangelog2 = $showChangelog")
                showChangelog = ChangelogUtil.shouldShowChangelogOnStart(storage, versionName, setup.versionFormatter)
                println("showChangelog3 = $showChangelog")
                if (showChangelog?.isInitialVersion == true) {
                    println("Updating last shown: ${showChangelog!!.currentVersion}")
                    scope.launch(Dispatchers.IO) {
                        storage.saveLastShownVersion(showChangelog!!.currentVersion)
                    }
                }
            }
        }

        showChangelog
            ?.takeIf { it.shouldShow }
            ?.let { data ->
                val filter = object : IChangelogFilter {
                    override fun keep(release: DataItemRelease): Boolean {
                        return release.versionCode >= data.lastShownVersion && (setup.filter?.keep(
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
                        storage.saveLastShownVersion(data.currentVersion)
                        showChangelog = ShowChangelog(data.currentVersion, data.currentVersion)
                    }
                }
            }
    }

    @Composable
    fun ShowChangelogDialog(
        setup: ChangelogSetup,
        onDismiss: () -> Unit = {}
    ) {
        val context = LocalContext()
        val changelog = remember { mutableStateOf<ChangelogData?>(null) }
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                val data = ChangelogUtil.read(context, setup.changelogId, setup.versionFormatter)
                changelog.value = filterAndSort(data, setup)
            }
        }

        val data = changelog.value
        if (data != null && !data.isEmpty()) {
            val openDialog = remember { mutableStateOf(true) }
            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        onDismiss()
                        openDialog.value = false
                    },
                    title = {
                        Text(text = setup.texts.dialogTitle)
                    },
                    text = {
                        Changelog(data, setup)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDismiss()
                                openDialog.value = false
                            }) {
                            Text(setup.texts.dialogButtonDismiss)
                        }
                    }
                )
            }
        }
    }
}

object ChangelogDefaults {

    @Composable
    fun tagNameFormatter() = @Composable { tag: String ->
        tag.uppercase()
    }

    @Composable
    fun tagColorProvider() = @Composable { tag: String ->
        when (tag.lowercase()) {
            "bug",
            "bugfix" -> MaterialTheme.colorScheme.error
            "new" -> if (MaterialTheme.colorScheme.background.luminance() > 0.5)
                Color(0, 100, 0) /* dark green */
            else
                Color(144, 238, 144) /* light green */
            else -> Color.Unspecified
        }
    }


    @Composable
    fun sorter() = { items: List<DataItem> ->
        items
            .sortedBy { it.tag }
            .sortedBy {
                when (it.tag) {
                    "new" -> 0
                    "bug",
                    "bugfix" -> 2
                    else -> 1
                }
            }
    }

    fun filter(filter: String, keepEmptyFilterRows: Boolean = true): IChangelogFilter {
        return object : IChangelogFilter {
            override fun keep(release: DataItemRelease): Boolean {
                if (release.filter == null)
                    return true
                return release.filter.contains(
                    filter,
                    true
                ) //&& release.items.count { keep(release, it) } > 0
            }

            override fun keep(release: DataItemRelease, item: DataItem): Boolean {
                return release.filter?.contains(filter, true) == true ||
                        item.filter?.contains(
                            filter,
                            true
                        ) ?: keepEmptyFilterRows
            }
        }
    }

    @Composable
    fun texts(
        dialogTitle: String = stringResource(Res.string.changelog_dialog_title),
        dialogButtonDismiss: String = stringOk(),
        buttonShowMore: String =  stringResource(Res.string.changelog_button_show_more)
    ) = ChangelogSetup.Texts(
        dialogTitle = dialogTitle,
        dialogButtonDismiss = dialogButtonDismiss,
        buttonShowMore = buttonShowMore
    )

    @Composable
    fun defaultItemRelease(modifier: Modifier, item: DataItemRelease) {
        ChangelogItemRelease(modifier, item)
    }

    @Composable
    fun defaultItem(
        item: DataItem,
        width: Dp? = 64.dp,
        tagAlignment: Alignment.Horizontal = Alignment.Start,
        tagColorProvider: @Composable (String) -> Color = tagColorProvider(),
        tagNameFormatter: @Composable (String) -> String = tagNameFormatter()
    ) {
        ChangelogItem(item = item, tagAlignment = tagAlignment) {
            ChangelogTag(item, width, tagColorProvider, tagNameFormatter)
        }
    }

    @Composable
    fun defaultItemMore(modifier: Modifier, setup: ChangelogSetup, onClick: () -> Unit) {
        ChangelogItemMore(modifier, setup, onClick)
    }

    @Composable
    fun renderer(
        itemRelease: @Composable (modifier: Modifier, item: DataItemRelease, setup: ChangelogSetup) -> Unit = { modifier, item, setup ->
            defaultItemRelease(modifier, item)
        },
        item: @Composable (modifier: Modifier, item: DataItem, setup: ChangelogSetup) -> Unit = { modifier, item, setup ->
            defaultItem(item)
        },
        itemShowMore: @Composable (modifier: Modifier, setup: ChangelogSetup, onClick: () -> Unit) -> Unit = { modifier, setup, onClick ->
            defaultItemMore(modifier, setup, onClick)
        }
    ) = ChangelogSetup.Renderer(
        itemRelease,
        item,
        itemShowMore
    )
}