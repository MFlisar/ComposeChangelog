package com.michaelflisar.composechangelog.interfaces

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.michaelflisar.composechangelog.classes.ChangelogSetup
import com.michaelflisar.composechangelog.data.XMLTag

interface IChangelogItemRenderer {

    fun canRender(item: XMLTag): Boolean

    @Composable
    fun headerTag(): HeaderTag?

    @Composable
    fun render(setup: ChangelogSetup, item: XMLTag)

    class HeaderTag(
        val text: String,
        val color: Color
    )

}