package com.michaelflisar.composechangelog.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            .padding(vertical = 2.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = item.text,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(8.dp))
        ItemTag(item, setup)
    }
}