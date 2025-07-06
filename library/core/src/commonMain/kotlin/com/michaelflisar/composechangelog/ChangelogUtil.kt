package com.michaelflisar.composechangelog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.michaelflisar.composechangelog.data.XMLTag
import com.michaelflisar.composechangelog.interfaces.IChangelogItemRenderer

object ChangelogUtil {

    internal fun findRenderer(tag: XMLTag): IChangelogItemRenderer? {
        return Changelog.RENDERERS.find { it.canRender(tag) }
    }

    @Composable
    internal fun rememberRenderer(tag: XMLTag): IChangelogItemRenderer? {
        return remember { findRenderer(tag) }
    }

    fun ensureAllTagsSupported(
        setup: Changelog.Setup,
        supportedTags: List<String>,
        xmlTags: List<XMLTag>,
    ) {
        if (setup.skipUnknownTags)
            return
        val unsupported = xmlTags.filter { tag ->
            supportedTags.none { it.equals(tag.tag, true) }
        }
        if (unsupported.isEmpty())
            return
        throw RuntimeException("Unsupported tags found: ${unsupported.joinToString { it.tag }}")
    }
}