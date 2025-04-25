package com.michaelflisar.composechangelog.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.LazyScrollContainer
import com.michaelflisar.composechangelog.classes.ChangelogSetup
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.rememberRenderer
import com.michaelflisar.composechangelog.renderer.ReleaseRenderer

@Composable
fun Changelog(
    releases: List<ChangelogReleaseItem>,
    setup: ChangelogSetup
) {
    LazyScrollContainer(
        state = rememberLazyListState(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        releases
            .forEach { release ->
                item {
                    OutlinedCard {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReleaseRenderer.render(release)
                            release.items.forEach { item ->
                                val renderer = rememberRenderer(item)
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
}