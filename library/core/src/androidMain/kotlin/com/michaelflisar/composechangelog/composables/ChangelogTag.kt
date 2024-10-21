package com.michaelflisar.composechangelog.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.classes.DataItem

@Composable
internal fun ChangelogTag(
    item: DataItem,
    tagWidth: Dp?,
    tagColorProvider: @Composable (String) -> Color,
    tagNameFormatter: @Composable (String) -> String
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tagNameFormatter(item.tag),
            style = MaterialTheme.typography.bodySmall,
            color = tagColorProvider(item.tag),
            fontWeight = FontWeight.Bold,
            modifier = (tagWidth?.let { Modifier.width(it) } ?: Modifier).then(
                Modifier.clip(MaterialTheme.shapes.extraSmall)
                    .background(tagColorProvider(item.tag).copy(alpha = .25f))
                    .padding(4.dp)
            ),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
        // trick to align the tag with the FIRST LINE of the text
        Text(" ", style = MaterialTheme.typography.bodyMedium)
    }
}