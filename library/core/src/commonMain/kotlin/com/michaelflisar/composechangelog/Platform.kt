package com.michaelflisar.composechangelog

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItemRelease

@Composable
expect fun stringOk() : String

@Composable
internal expect fun ShowChangelogDialog(
    data: ChangelogData,
    setup: ChangelogSetup,
    onDismiss: () -> Unit
)

@Composable
internal expect fun LazyScrollContainer(state: LazyListState, content: LazyListScope.() -> Unit)

internal expect suspend fun ChangelogUtil.readFile(
    logFileReader: suspend () -> ByteArray,
    versionFormatter: ChangelogVersionFormatter,
    sorter: Comparator<DataItemRelease>? = null
): ChangelogData