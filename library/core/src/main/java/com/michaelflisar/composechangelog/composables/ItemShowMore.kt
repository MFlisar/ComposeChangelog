package com.michaelflisar.composechangelog.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.ChangelogSetup

@Composable
fun ItemShowMore(
    modifier: Modifier = Modifier,
    setup: ChangelogSetup,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        TextButton(
            onClick = onClick
        ) {
            Text(setup.texts.buttonShowMore)
        }
    }

}