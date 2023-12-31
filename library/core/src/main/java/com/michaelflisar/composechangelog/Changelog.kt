package com.michaelflisar.composechangelog

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
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
import kotlin.math.pow

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
        var showChangelog by rememberSaveable(setup) { mutableStateOf<ShowChangelog?>(null) }
        LaunchedEffect(setup) {
            if (showChangelog == null) {
                showChangelog = ChangelogUtil.shouldShowChangelogOnStart(context, storage)
            }
        }
        println("showChangelog = $showChangelog")
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

enum class VersionFormat {

    /**
     * 10^2: Major Version
     * 10^0: Minor Version
     *
     * Examples:
     * - 1 => 0.1
     * - 10 => 0.10
     * - 100 => 1.0
     */
    MajorMinor,

    /**
     * 10^4: Major Version
     * 10^2: Minor Version
     * 10^0: Patch Version
     *
     * Examples:
     * - 1 => 0.0.1
     * - 10 => 0.0.10
     * - 100 => 0.1.0
     * - 1000 => 0.10.0
     * - 10000 => 1.0.0
     */
    MajorMinorPatch,

    /**
     * 10^6: Major Version
     * 10^4: Minor Version
     * 10^2: Patch Version
     * 10^0: Candidate Version (only displayed if != 0)
     *
     * Examples:
     * - 1 => 0.0.0-01
     * - 10 => 0.0.0-10
     * - 100 => 0.0.1
     * - 101 => 0.0.1-01
     * - 1000 => 0.0.10
     * - 10000 => 0.1.0
     * - 10000  => 0.10.0
     * - 100000  => 1.0.0
     */
    MajorMinorPatchCandidate
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
    fun versionFormatter(format: VersionFormat = VersionFormat.MajorMinor, prefix: String = "") =
        @Composable { versionCode: Int ->
            if (versionCode >= 0) {
                when (format) {
                    VersionFormat.MajorMinor -> {
                        val major = floor((versionCode.toFloat() / 100f)).toInt()
                        val minor = versionCode - major * 100
                        "$prefix$major.$minor"
                    }

                    VersionFormat.MajorMinorPatch -> {
                        var tmp = versionCode.toFloat()
                        val major = floor((tmp / 10f.pow(4))).toInt()
                        tmp -= major * 10f.pow(6)
                        val minor = floor((tmp / 10f.pow(2))).toInt()
                        tmp -= major * 10f.pow(4)
                        val patch = tmp.toInt()
                        "$prefix$major.$minor.$patch"
                    }

                    VersionFormat.MajorMinorPatchCandidate -> {
                        var tmp = versionCode.toFloat()
                        val major = floor((tmp / 10f.pow(6))).toInt()
                        tmp -= major * 10f.pow(6)
                        val minor = floor((tmp / 10f.pow(4))).toInt()
                        tmp -= major * 10f.pow(4)
                        val patch = floor((tmp / 10f.pow(2))).toInt()
                        tmp -= patch * 10f.pow(2)
                        val candidate = tmp.toInt()
                        val version = "$prefix$major.$minor.$patch"
                        if (candidate == 0) {
                            version
                        } else String.format("%s-%02d", version, candidate)
                    }
                }
            } else "$prefix$versionCode"
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
        dialogTitle: String = stringResource(R.string.changelog_dialog_title),
        dialogButtonDismiss: String = stringResource(android.R.string.ok),
        buttonShowMore: String = stringResource(R.string.changelog_button_show_more)
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