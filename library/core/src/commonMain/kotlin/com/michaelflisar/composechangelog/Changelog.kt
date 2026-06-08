package com.michaelflisar.composechangelog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.ChangelogState
import com.michaelflisar.composechangelog.classes.ChangelogTextFormatter
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.format.ChangelogVersionFormatter
import com.michaelflisar.composechangelog.interfaces.IChangelogItemRenderer
import com.michaelflisar.composechangelog.interfaces.IChangelogReleaseRenderer
import com.michaelflisar.composechangelog.renderer.ReleaseRenderer
import com.michaelflisar.composechangelog.renderer.SimpleRenderer
import com.michaelflisar.kmp.platformcontext.platformIO
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

    /**
     * sets the renderer for releases (default is [ReleaseRenderer])
     */
    fun setReleaseRenderer(renderer: IChangelogReleaseRenderer) {
        RELEASE_RENDERER = renderer
    }

    class Setup internal constructor(
        val logFileReader: suspend () -> ByteArray,
        val textFormatter: ChangelogTextFormatter,
        val versionFormatter: ChangelogVersionFormatter,
        val skipUnknownTags: Boolean,
        val textMore: String,
    )

}

/**
 * Main entry point for the changelog. It will read the changelog data and display it.
 * You can customize the release container and the loading state.
 *
 * @param state the state of the changelog, which contains the minimum visible release version and a function to hide the changelog if there are no releases to show
 * @param setup the setup for the changelog, which contains the log file reader, text formatter, version formatter, and other settings
 * @param modifier the modifier for the changelog
 * @param lazyListState the state for the lazy list, which can be used to control the scroll position
 * @param releaseContainer a composable function that wraps the release content, which can be used to customize the appearance of the release (default is an outlined card with padding)
 */
@Composable
fun Changelog(
    state: ChangelogState,
    setup: Changelog.Setup,
    modifier: Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
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
        is ChangelogData.Data -> Changelog(d.items, setup, modifier, lazyListState, releaseContainer)
        ChangelogData.Loading -> Box(modifier, contentAlignment = Alignment.Center) { loading() }
    }
}

/**
 * Displays the list of releases and their items. It uses the release renderer to display the release information and the item renderers to display the items.
 *
 * @param releases the list of releases to display
 * @param setup the setup for the changelog, which contains the text formatter and other settings
 * @param modifier the modifier for the changelog
 * @param lazyListState the state for the lazy list, which can be used to control the scroll position
 * @param releaseContainer a composable function that wraps the release content, which can be used to customize the appearance of the release (default is an outlined card with padding)
 */
@Composable
fun Changelog(
    releases: List<ChangelogReleaseItem>,
    setup: Changelog.Setup,
    modifier: Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    releaseContainer: @Composable (content: @Composable () -> Unit) -> Unit = { it() },
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
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

/**
 * Reads the changelog data from the file and filters it based on the minimum visible release version. It returns a mutable state that contains the changelog data, which can be either loading or the actual data.
 *
 * @param state the state of the changelog, which contains the minimum visible release version and a function to hide the changelog if there are no releases to show
 * @param setup the setup for the changelog, which contains the log file reader, version formatter, and other settings
 * @param filter a function that can be used to filter the releases based on custom criteria (default is to include all releases)
 * @return a mutable state that contains the changelog data, which can be either loading or the actual data
 */
@Composable
fun rememberChangelogData(
    state: ChangelogState,
    setup: Changelog.Setup,
    filter: ((ChangelogReleaseItem) -> Boolean) = { true },
): MutableState<ChangelogData> {
    val data = remember { mutableStateOf<ChangelogData>(ChangelogData.Loading) }
    LaunchedEffect(Unit) {
        withContext(platformIO) {
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