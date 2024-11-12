package com.michaelflisar.composechangelog.composables

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.michaelflisar.composechangelog.ChangelogSetup
import com.michaelflisar.composechangelog.LazyScrollContainer
import com.michaelflisar.composechangelog.classes.ChangelogData

@Composable
fun Changelog(
    changelog: ChangelogData,
    setup: ChangelogSetup
) {
    val expandedReleases = remember { mutableStateListOf<Int>() }
    val releases = changelog.releases

    var idMore = -1
    LazyScrollContainer(
        state = rememberLazyListState()
    ) {
        releases.forEachIndexed { index, item ->

            item(item.id) {
                setup.renderer.itemRelease(
                    Modifier.animateItem(
                        fadeInSpec = null,
                        fadeOutSpec = null
                    ), item, setup
                )
            }

            val summaryItems = item.getSummaryItems()
            val nonSummaryItems = item.getNonSummaryItems()
            summaryItems.forEach {
                item(it.id) {
                    setup.renderer.item(
                        Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                        it,
                        setup
                    )
                }
            }

            val expanded = !setup.useShowMoreButtons || expandedReleases.contains(index)

            if (!expanded && summaryItems.isNotEmpty() && nonSummaryItems.isNotEmpty()) {
                item(idMore--) {
                    setup.renderer.itemShowMore(
                        Modifier.animateItem(
                            fadeInSpec = null,
                            fadeOutSpec = null
                        ), setup
                    ) {
                        expandedReleases.add(index)
                    }
                }
            }

            if (expanded || summaryItems.isEmpty()) {
                nonSummaryItems.forEach {
                    item(it.id) {
                        setup.renderer.item(
                            Modifier.animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null
                            ), it, setup
                        )
                    }
                }
            }
        }
    }
}