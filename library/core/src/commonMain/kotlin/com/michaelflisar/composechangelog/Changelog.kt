package com.michaelflisar.composechangelog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.ChangelogState
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.interfaces.IChangelogItemRenderer
import com.michaelflisar.composechangelog.interfaces.IChangelogReleaseRenderer
import com.michaelflisar.composechangelog.renderer.ReleaseRenderer
import com.michaelflisar.composechangelog.renderer.SimpleRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Changelog {

    internal val RENDERERS = mutableListOf<IChangelogItemRenderer>(
        SimpleRenderer(
            tag = "news",
            label  ="News",
            color = {
                if (MaterialTheme.colorScheme.background.luminance() > 0.5)
                    Color(0, 100, 0) /* dark green */
                else
                    Color(144, 238, 144) /* light green */
            }
        ),
        SimpleRenderer(
            tag = "improvements",
            label = "Improvements"
        ),
        SimpleRenderer(
            tag = "changes",
            label = "Changes"
        ),
        SimpleRenderer(
            tag = "bugfixes",
            label = "Bugfixes",
            color = { MaterialTheme.colorScheme.error }
        )
    )

    internal var RELEASE_RENDERER: IChangelogReleaseRenderer = ReleaseRenderer()

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

    fun setReleaseRenderer(renderer: IChangelogReleaseRenderer) {
        RELEASE_RENDERER = renderer
    }

    data class Setup internal constructor(
        val logFileReader: suspend () -> ByteArray,
        val textFormatter: @Composable (text: String) -> AnnotatedString,
        val versionFormatter: ChangelogVersionFormatter,
        val skipUnknownTags: Boolean,
        val textMore: String,
    )

}

@Composable
fun Changelog(
    state: ChangelogState,
    setup: Changelog.Setup,
    modifier: Modifier,
    releaseContainer: @Composable (content: @Composable () -> Unit) -> Unit = {
        OutlinedCard {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                it()
            }
        }
    },
    loading: @Composable () -> Unit = { LinearProgressIndicator() },
) {
    val releases = rememberChangelogData(state, setup)
    when (val d = releases.value) {
        is ChangelogData.Data -> Changelog(d.items, setup, modifier, releaseContainer)
        ChangelogData.Loading -> Box(modifier) { loading() }
    }
}

@Composable
fun Changelog(
    releases: List<ChangelogReleaseItem>,
    setup: Changelog.Setup,
    modifier: Modifier,
    releaseContainer: @Composable (content: @Composable () -> Unit) -> Unit = { it() },
) {
    LazyScrollContainer(
        modifier = modifier,
        state = rememberLazyListState(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        releases
            .forEach { release ->
                item {
                    releaseContainer {
                        Changelog.RELEASE_RENDERER.render(release)
                        release.items.forEach { item ->
                            val renderer = ChangelogUtil.rememberRenderer(item)
                            if (renderer == null) {
                                if (!setup.skipUnknownTags) {
                                    throw RuntimeException("No renderer found for item: $item")
                                }
                            } else {
                                renderer.render(setup, item)
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun rememberChangelogData(
    state: ChangelogState,
    setup: Changelog.Setup,
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



