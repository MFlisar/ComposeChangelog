package com.michaelflisar.composechangelog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.ChangelogSetup
import com.michaelflisar.composechangelog.classes.ChangelogState
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.data.XMLTag
import com.michaelflisar.composechangelog.interfaces.IChangelogItemRenderer
import com.michaelflisar.composechangelog.renderer.SimpleRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Changelog {

    internal val RENDERERS = mutableListOf<IChangelogItemRenderer>(
        SimpleRenderer(
            tag = "news",
            color = {
                if (MaterialTheme.colorScheme.background.luminance() > 0.5)
                    Color(0, 100, 0) /* dark green */
                else
                    Color(144, 238, 144) /* light green */
            },
            region = {
                SimpleRenderer.RenderRegion("New", Icons.Default.Info, it, it)
            }
        ),
        SimpleRenderer(
            tag = "improvements",
            region = { SimpleRenderer.RenderRegion("Improvements", Icons.Default.PriorityHigh) }
        ),
        SimpleRenderer(
            tag = "changes",
            region = { SimpleRenderer.RenderRegion("Changes", Icons.Default.Edit) }
        ),
        SimpleRenderer(
            tag = "bugfixes",
            color = { MaterialTheme.colorScheme.error },
            region = { SimpleRenderer.RenderRegion("Bugfixes", Icons.Default.BugReport, it, it) }
        )
    )

    /**
     * removes all renderers (also the default ones!)
     */
    fun clearRenderers() {
        RENDERERS.clear()
    }

    /**
     * adds a new renderer to the list of renderers
     */
    fun registerRenderer(renderer: IChangelogItemRenderer) {
        RENDERERS.add(renderer)
    }

    fun ensureAllTagsSupported(
        setup: ChangelogSetup,
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

internal fun findRenderer(tag: XMLTag): IChangelogItemRenderer? {
    return Changelog.RENDERERS.find { it.canRender(tag) }
}


@Composable
internal fun rememberRenderer(tag: XMLTag): IChangelogItemRenderer? {
    return remember { findRenderer(tag) }
}

@Composable
fun rememberChangelogData(
    state: ChangelogState,
    setup: ChangelogSetup,
    filter: ((ChangelogReleaseItem) -> Boolean) = { true },
): MutableState<ChangelogData> {
    val data = remember { mutableStateOf<ChangelogData>(ChangelogData.Loading) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val items = Changelog
                .readFile(setup.logFileReader, setup.versionFormatter)
                .filter { it.versionCode >= state.minimumVisibleReleaseVersion.value }
                .filter(filter)
            val d = ChangelogData.Data(items)
            data.value = d
            if (d.items.isEmpty()) {
                state.hide()
            }
        }
    }
    return data
}

@Composable
fun rememberSubXMLTags(xmlTag: XMLTag): State<List<XMLTag>> {
    return remember(xmlTag.innerText) {
        derivedStateOf {
            val subItems = xmlTag.children()
            subItems
        }
    }
}