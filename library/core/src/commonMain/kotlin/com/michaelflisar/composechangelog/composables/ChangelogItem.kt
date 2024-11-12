package com.michaelflisar.composechangelog.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.classes.DataItem

@Composable
internal fun ChangelogItem(
    modifier: Modifier = Modifier,
    item: DataItem,
    tagAlignment: Alignment.Horizontal,
    tag: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        if (tagAlignment == Alignment.Start) {
            tag()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            modifier = Modifier.weight(1f),
            text = item.text,
            style = MaterialTheme.typography.bodyMedium
        )
        if (tagAlignment == Alignment.End) {
            Spacer(modifier = Modifier.width(8.dp))
            tag()
        }
    }
}