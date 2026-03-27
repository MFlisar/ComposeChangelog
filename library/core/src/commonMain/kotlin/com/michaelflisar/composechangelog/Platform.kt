package com.michaelflisar.composechangelog

import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.format.ChangelogVersionFormatter

internal expect suspend fun Changelog.readFile(
    logFileReader: suspend () -> ByteArray,
    versionFormatter: ChangelogVersionFormatter,
): List<ChangelogReleaseItem>