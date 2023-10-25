package com.michaelflisar.composechangelog.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.ChangelogSetup
import com.michaelflisar.composechangelog.classes.DataItemRelease

@Composable
fun ItemRelease(
    modifier: Modifier = Modifier,
    item: DataItemRelease,
    setup: ChangelogSetup
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            setup.versionCodeFormatter(item.versionCode),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(item.date, style = MaterialTheme.typography.labelMedium, fontStyle = FontStyle.Italic)
    }
}

