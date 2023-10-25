package com.michaelflisar.composechangelog

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItem
import com.michaelflisar.composechangelog.classes.DataItemRelease
import com.michaelflisar.composechangelog.classes.ShowChangelog
import com.michaelflisar.composechangelog.composables.Changelog
import com.michaelflisar.composechangelog.composables.ItemRelease
import com.michaelflisar.composechangelog.composables.ItemReleaseRow
import com.michaelflisar.composechangelog.composables.ItemShowMore
import com.michaelflisar.composechangelog.interfaces.IChangelogFilter
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver
import com.michaelflisar.composechangelog.internal.ChangelogParserUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor

object Changelog {

    suspend fun read(
        context: Context,
        changelogResourceId: Int,
        sorter: Comparator<DataItemRelease>? = null
    ): ChangelogData {
        return ChangelogParserUtil.parse(context, changelogResourceId, sorter)
    }

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
        setup: ChangelogSetup = ChangelogDefaults.setup()
    ) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        var showChangelog by remember(setup) { mutableStateOf<ShowChangelog?>(null) }
        LaunchedEffect(setup) {
            showChangelog = ChangelogUtil.shouldShowChangelogOnStart(context, storage)
        }
        showChangelog
            ?.takeIf { it.shouldShow }
            ?.let {
                val filter = object : IChangelogFilter {
                    override fun keep(release: DataItemRelease): Boolean {
                        return release.versionCode >= it.lastShownVersion && (setup.filter?.keep(
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
                        storage.saveLastShownVersion(ChangelogUtil.getAppVersionCode(context))
                    }
                }
            }
    }

    @Composable
    fun ShowChangelogDialog(
        setup: ChangelogSetup = ChangelogDefaults.setup(),
        onDismiss: () -> Unit = {}
    ) {
        val context = LocalContext.current
        val changelog = remember { mutableStateOf<ChangelogData?>(null) }
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                val data = read(context, setup.changelogResourceId)
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
                        Button(
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

            "new" -> if (isSystemInDarkTheme()) Color(144, 238, 144) /* light green */ else Color(
                0,
                100,
                0
            ) /* dark green */
            else -> Color.Unspecified
        }
    }

    @Composable
    fun versionFormatter() = @Composable { versionCode: Int ->
        if (versionCode >= 0) {
            val major = floor((versionCode.toFloat() / 100f)).toInt()
            val minor = versionCode - major * 100
            "v" + major + "." + String.format("%02d", minor)
        } else "v$versionCode"
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
        dialogTitle: String = "Changelog",
        dialogButtonDismiss: String = stringResource(android.R.string.ok),
        buttonShowMore: String = "Show More"
    ) = ChangelogSetup.Texts(
        dialogTitle = dialogTitle,
        dialogButtonDismiss = dialogButtonDismiss,
        buttonShowMore = buttonShowMore
    )

    val DEFAULT_RENDERER = ChangelogSetup.Renderer(
        itemRelease = { modifier, item, setup ->
            ItemRelease(modifier, item, setup)
        },
        item = { modifier, item, setup ->
            ItemReleaseRow(modifier, item, setup)
        },
        itemShowMore = { modifier, setup, onClick ->
            ItemShowMore(modifier, setup, onClick)
        }
    )

    @Composable
    fun setup(
        changelogResourceId: Int = R.raw.changelog,
        texts: ChangelogSetup.Texts = texts(),
        useShowMoreButtons: Boolean = true,
        tagWidth: Dp? = 48.dp,
        tagColorProvider: @Composable (tag: String) -> Color = tagColorProvider(),
        tagNameFormatter: @Composable (tag: String) -> String = tagNameFormatter(),
        versionCodeFormatter: @Composable (versionCode: Int) -> String = versionFormatter(),
        sorter: ((items: List<DataItem>) -> List<DataItem>)? = sorter(),
        filter: IChangelogFilter? = null,
        renderer: ChangelogSetup.Renderer = DEFAULT_RENDERER
    ) = ChangelogSetup(
        changelogResourceId = changelogResourceId,
        texts = texts,
        useShowMoreButtons = useShowMoreButtons,
        tagWidth = tagWidth,
        tagColorProvider = tagColorProvider,
        tagNameFormatter = tagNameFormatter,
        versionCodeFormatter = versionCodeFormatter,
        filter = filter,
        sorter = sorter,
        renderer = renderer
    )
}