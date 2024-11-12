package com.michaelflisar.composechangelog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItemRelease
import com.michaelflisar.composechangelog.internal.ChangelogParserUtil

actual typealias Context = android.content.Context
actual typealias ChangelogID = Int

@Composable
actual fun stringOk() = stringResource(android.R.string.ok)

@Composable
internal actual fun LocalContext(): Context {
    return androidx.compose.ui.platform.LocalContext.current
}

internal actual suspend fun ChangelogUtil.read(
    context: Context,
    changelogID: ChangelogID,
    versionFormatter: ChangelogVersionFormatter,
    sorter: Comparator<DataItemRelease>?
): ChangelogData {
    return ChangelogParserUtil.parse(context, changelogID, versionFormatter, sorter)
}