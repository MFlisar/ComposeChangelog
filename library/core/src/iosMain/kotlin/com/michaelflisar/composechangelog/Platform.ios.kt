package com.michaelflisar.composechangelog

import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.defaults.XMLRegexParser
import com.michaelflisar.composechangelog.format.ChangelogVersionFormatter

internal actual suspend fun Changelog.readFile(
    logFileReader: suspend () -> ByteArray,
    versionFormatter: ChangelogVersionFormatter,
): List<ChangelogReleaseItem> {
    return XMLRegexParser.parse(logFileReader, versionFormatter)
}
