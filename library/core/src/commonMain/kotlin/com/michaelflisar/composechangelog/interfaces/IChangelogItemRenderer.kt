package com.michaelflisar.composechangelog.interfaces

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.data.XMLTag

interface IChangelogItemRenderer {

    /**
     * Checks if this renderer can render the given item. The first renderer that returns true will be used to render the item.
     */
    fun canRender(item: XMLTag): Boolean

    /**
     * If this item has a header tag, it will be used to display a tag in the release header. This is optional and can be used to display additional information about the item type.
     */
    @Composable
    fun headerTag(): HeaderTag?

    /**
     * Renders the given item. The setup is passed to provide additional information about the changelog
     */
    @Composable
    fun render(setup: Changelog.Setup, item: XMLTag)

    /**
     * a header tag is used to display a tag in the release header. This is optional and can be used to display additional information about the item type.
     */
    class HeaderTag(
        val text: String,
        val color: Color,
    )
}