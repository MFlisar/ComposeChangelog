package com.michaelflisar.composechangelog.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.ChangelogSetup
import com.michaelflisar.composechangelog.classes.DataItem

@Composable
fun ItemReleaseRow(
    modifier: Modifier = Modifier,
    item: DataItem,
    setup: ChangelogSetup
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = setup.tagNameFormatter(item.tag),
                style = MaterialTheme.typography.bodySmall,
                color = setup.tagColorProvider(item.tag),
                fontWeight = FontWeight.Bold,
                modifier = setup.tagWidth?.let { Modifier.width(it) } ?: Modifier,
                maxLines = 1
            )
            // trick to align the tag with the FIRST LINE of the text
            Text(" ", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(item.text, style = MaterialTheme.typography.bodyMedium)
    }
}