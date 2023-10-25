package com.michaelflisar.composechangelog.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.ChangelogSetup
import com.michaelflisar.composechangelog.classes.ChangelogData

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Changelog(
    changelog: ChangelogData,
    setup: ChangelogSetup
) {
    val expandedReleases = remember { mutableStateListOf<Int>() }
    val releases = changelog.releases

    var idMore = -1
    LazyColumn(
        state = rememberLazyListState()
    ) {
        releases.forEachIndexed { index, item ->

            item(item.id) {
                setup.renderer.itemRelease(Modifier.animateItemPlacement(), item, setup)
            }

            val summaryItems = item.getSummaryItems()
            val nonSummaryItems = item.getNonSummaryItems()
            summaryItems.forEach {
                item(it.id) {
                    setup.renderer.item(Modifier.animateItemPlacement(), it, setup)
                }
            }

            val expanded = !setup.useShowMoreButtons || expandedReleases.contains(index)

            if (!expanded && summaryItems.isNotEmpty() && nonSummaryItems.isNotEmpty()) {
                item(idMore--) {
                    setup.renderer.itemShowMore(Modifier.animateItemPlacement(), setup) {
                        expandedReleases.add(index)
                    }
                }
            }

            if (expanded || summaryItems.isEmpty()) {
                nonSummaryItems.forEach {
                    item(it.id) {
                        setup.renderer.item(Modifier.animateItemPlacement(), it, setup)
                    }
                }
            }
        }
    }
}