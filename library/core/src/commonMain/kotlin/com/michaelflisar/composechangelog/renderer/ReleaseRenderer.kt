package com.michaelflisar.composechangelog.renderer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.findRenderer

object ReleaseRenderer {

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun render(release: ChangelogReleaseItem) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // all existing types...
                val types = release.items.mapNotNull {
                    val renderer = findRenderer(it)
                    renderer?.headerTag()
                }
                    .distinct()
                    .sortedBy { it.text }

                val fontTags = MaterialTheme.typography.bodySmall.copy(
                    //fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.8f,
                    //lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 0.8f,
                )

                types.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            modifier = Modifier.size(with(LocalDensity.current) { fontTags.lineHeight.toDp() }),
                            imageVector = Icons.Default.Circle,
                            contentDescription = null,
                            tint = it.color
                        )
                        Text(text = it.text.uppercase(), style = fontTags)
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = release.title ?: release.versionName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = release.date,
                    style = MaterialTheme.typography.labelMedium,
                    //fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = LocalContentColor.current.copy(alpha = .6f)
                )
            }
        }
    }
}