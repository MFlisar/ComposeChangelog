package com.michaelflisar.composechangelog

import androidx.compose.runtime.Composable
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItemRelease
import com.michaelflisar.composechangelog.internal.ChangelogParserUtil
import java.io.File

actual abstract class Context
actual typealias ChangelogID = File

@Composable
actual fun stringOk() = "OK"

object NoContext: Context()

@Composable
internal actual fun LocalContext(): Context {
    return NoContext
}

internal actual suspend fun ChangelogUtil.read(
    context: Context,
    changelogID: ChangelogID,
    versionFormatter: ChangelogVersionFormatter,
    sorter: Comparator<DataItemRelease>?
): ChangelogData {
    return ChangelogParserUtil.parse(changelogID, versionFormatter, sorter)
}