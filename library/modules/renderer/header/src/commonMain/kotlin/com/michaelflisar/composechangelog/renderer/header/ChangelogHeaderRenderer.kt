package com.michaelflisar.composechangelog.renderer.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.ChangelogUtil
import com.michaelflisar.composechangelog.data.XMLTag
import com.michaelflisar.composechangelog.interfaces.IChangelogItemRenderer
import com.michaelflisar.composechangelog.interfaces.IChangelogItemRenderer.HeaderTag
import com.michaelflisar.composechangelog.renderer.SimpleRenderer.Companion.RenderItem

class ChangelogHeaderRenderer(
    val titleAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    val iconPlacement: IconPlacement = IconPlacement.Top,
    val iconProvider: @Composable (icon: String?) -> Unit = {},
) : IChangelogItemRenderer {

    enum class IconPlacement {
        Start,
        Top
    }

    companion object {

        private const val ATTR_ICON = "icon"

        private const val TAG_TITLE = "title"
        private const val TAG_INFOS = "infos"
        private const val TAG_ITEM = "item"
    }

    override fun canRender(item: XMLTag): Boolean {
        return item.tag.equals("header", true)
    }

    @Composable
    override fun headerTag(): HeaderTag? = null

    @Composable
    override fun render(setup: Changelog.Setup, item: XMLTag) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            val icon = remember(item) {
                item.attributes.find { it.name.equals(ATTR_ICON, true) }?.value
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top
            ) {
                if (iconPlacement == IconPlacement.Start) {
                    iconProvider(icon)
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (iconPlacement == IconPlacement.Top) {
                        Box(
                            modifier = Modifier.align(titleAlignment)
                        ) {
                            iconProvider(icon)
                        }
                    }

                    val subItems = item.children

                    if (!setup.skipUnknownTags) {
                        ChangelogUtil.ensureAllTagsSupported(
                            setup,
                            listOf(TAG_TITLE, TAG_INFOS, TAG_ITEM),
                            subItems
                        )
                    }

                    subItems.forEach { subItem ->
                        if (subItem.tag.equals(TAG_TITLE, true)) {
                            Text(
                                modifier = Modifier.align(titleAlignment),
                                text = setup.textFormatter(subItem.innerText),
                                style = MaterialTheme.typography.titleMedium
                            )
                        } else if (subItem.tag.equals(TAG_ITEM, true)) {
                            // single item takes alignment from title...
                            Box(
                                modifier = Modifier.align(titleAlignment)
                            ) {
                                RenderItem(setup, subItem, list = false)
                            }
                        } else if (subItem.tag.equals(TAG_INFOS, true)) {
                            val subItems2 = subItem.children
                            if (!setup.skipUnknownTags) {
                                ChangelogUtil.ensureAllTagsSupported(
                                    setup,
                                    listOf(TAG_ITEM),
                                    subItems2
                                )
                            }
                            subItems2.forEach { subItem2 ->
                                RenderItem(setup, subItem2)
                            }
                        }
                    }
                }
            }
        }
    }
}