package com.michaelflisar.composechangelog.defaults

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun DefaultLazyScrollContainer(
    modifier: Modifier,
    state: LazyListState,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = state,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        content()
    }
}