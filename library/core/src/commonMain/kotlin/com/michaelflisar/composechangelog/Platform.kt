package com.michaelflisar.composechangelog

import androidx.compose.runtime.Composable
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItemRelease

expect abstract class Context
expect class ChangelogID

@Composable
expect fun stringOk() : String

@Composable
internal expect fun LocalContext(): Context

internal expect suspend fun ChangelogUtil.read(
    context: Context,
    changelogID: ChangelogID,
    versionFormatter: ChangelogVersionFormatter,
    sorter: Comparator<DataItemRelease>? = null
): ChangelogData