package com.michaelflisar.composechangelog

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItemRelease

expect abstract class Context
expect class ChangelogID

@Composable
expect fun stringOk() : String

@Composable
internal expect fun LocalContext(): Context

@Composable
internal expect fun ShowChangelogDialog(
    visible: MutableState<Boolean>,
    data: ChangelogData,
    setup: ChangelogSetup,
    onDismiss: () -> Unit
)

@Composable
internal expect fun LazyScrollContainer(state: LazyListState, content: LazyListScope.() -> Unit)

internal expect suspend fun ChangelogUtil.read(
    context: Context,
    changelogID: ChangelogID,
    versionFormatter: ChangelogVersionFormatter,
    sorter: Comparator<DataItemRelease>? = null
): ChangelogData