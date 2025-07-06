package com.michaelflisar.composechangelog.renderer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.ChangelogUtil
import com.michaelflisar.composechangelog.data.XMLTag
import com.michaelflisar.composechangelog.interfaces.IChangelogItemRenderer
import com.michaelflisar.composechangelog.interfaces.IChangelogItemRenderer.HeaderTag

/**
 * a simple renderer that can render a xml tag
 *
 * this renderer supports following child structure:
 *
 * <item>...</item>         // 0 to n times
 * <more>
 *     <item>...</item>     // 0 to n times
 * </more>
 *
 * the <item> tag is used to render a single item and is the one that is rendered by this renderer
 *
 * tag: the tag to render
 * tag: the label for this tag
 * showTag: if true, the tag will be shown above the release header (colored dot + text)
 * color: the color of the tag (used for the dot in the release header and is passed on to the next region lambda)
 */
class SimpleRenderer(
    val tag: String,
    val label: String,
    val showTag: Boolean = true,
    val color: @Composable () -> Color = { LocalContentColor.current }
) : IChangelogItemRenderer {

    companion object {

        private const val TAG_ITEM = "item"
        private const val TAG_MORE = "more"

        @Composable
        fun RenderRegion(
            label: String,
            textColor: Color = LocalContentColor.current,
        ) {
            Text(text = label, style = MaterialTheme.typography.titleMedium, color = textColor)
        }

        @Composable
        fun RenderItem(setup: Changelog.Setup, item: XMLTag, list: Boolean = true) {
            Row {
                if (list) {
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = setup.textFormatter(item.innerText),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        @Composable
        fun ColumnScope.RenderMore(
            setup: Changelog.Setup,
            subItems: List<XMLTag>,
            item: @Composable (item: XMLTag) -> Unit,
        ) {
            val expanded = rememberSaveable { mutableStateOf(false) }
            AnimatedVisibility(
                visible = !expanded.value,
            ) {
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    TextButton(
                        onClick = { expanded.value = true }
                    ) {
                        Text(setup.textMore)
                    }
                }
            }
            AnimatedVisibility(
                visible = expanded.value,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    subItems.forEach { item(it) }
                }
            }

        }
    }

    override fun canRender(item: XMLTag): Boolean {
        return item.tag.equals(tag, true)
    }

    @Composable
    override fun headerTag(): HeaderTag? {
        if (!showTag)
            return null
        return HeaderTag(
            text = tag,
            color = color()
        )
    }

    @Composable
    override fun render(setup: Changelog.Setup, item: XMLTag) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val subItems = item.children

            RenderRegion(label, color())

            if (!setup.skipUnknownTags) {
                ChangelogUtil.ensureAllTagsSupported(setup, listOf(TAG_MORE, TAG_ITEM), subItems)
            }

            subItems.forEach { subItem ->
                if (subItem.tag.equals(TAG_ITEM, true)) {
                    RenderItem(setup, subItem)
                } else if (subItem.tag.equals(TAG_MORE, true)) {
                    val subItems2 = subItem.children
                    if (!setup.skipUnknownTags) {
                        ChangelogUtil.ensureAllTagsSupported(setup, listOf(TAG_ITEM), subItems2)
                    }
                    RenderMore(setup, subItems2, { RenderItem(setup, it) })
                }
            }
        }
    }
}