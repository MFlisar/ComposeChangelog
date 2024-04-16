package com.michaelflisar.composechangelog.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.ChangelogSetup
import com.michaelflisar.composechangelog.classes.DataItem

@Composable
fun ItemTag(
    item: DataItem,
    setup: ChangelogSetup
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = setup.tagNameFormatter(item.tag),
            style = MaterialTheme.typography.bodySmall,
            color = setup.tagColorProvider(item.tag),
            fontWeight = FontWeight.Bold,
            modifier = (setup.tagWidth?.let { Modifier } ?: Modifier).then(
                Modifier.clip(MaterialTheme.shapes.extraSmall)
                    .background(setup.tagColorProvider(item.tag).copy(alpha = .25f))
                    .padding(4.dp)
            ),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
        // trick to align the tag with the FIRST LINE of the text
        Text(" ", style = MaterialTheme.typography.bodyMedium)
    }
}