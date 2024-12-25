package com.michaelflisar.composechangelog

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.classes.DataItem
import com.michaelflisar.composechangelog.classes.DataItemRelease
import com.michaelflisar.composechangelog.composables.ChangelogItem
import com.michaelflisar.composechangelog.composables.ChangelogItemMore
import com.michaelflisar.composechangelog.composables.ChangelogItemRelease
import com.michaelflisar.composechangelog.composables.ChangelogTag
import com.michaelflisar.composechangelog.interfaces.IChangelogFilter
import com.michaelflisar.composechangelog.core.resources.Res
import com.michaelflisar.composechangelog.core.resources.changelog_button_show_more
import com.michaelflisar.composechangelog.core.resources.changelog_dialog_title
import org.jetbrains.compose.resources.stringResource

object ChangelogDefaults {

    @Composable
    fun setup(
        logFileReader: suspend () -> ByteArray,
        texts: ChangelogSetup.Texts = ChangelogDefaults.texts(),
        useShowMoreButtons: Boolean = true,
        versionFormatter: ChangelogVersionFormatter = DefaultVersionFormatter(),
        sorter: ((items: List<DataItem>) -> List<DataItem>)? = ChangelogDefaults.sorter(),
        filter: IChangelogFilter? = null,
        renderer: ChangelogSetup.Renderer = ChangelogDefaults.renderer()
    ) = ChangelogSetup(
        logFileReader = logFileReader,
        texts = texts,
        useShowMoreButtons = useShowMoreButtons,
        versionFormatter = versionFormatter,
        filter = filter,
        sorter = sorter,
        renderer = renderer
    )

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
                )
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
        buttonShowMore: String = stringResource(Res.string.changelog_button_show_more)
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