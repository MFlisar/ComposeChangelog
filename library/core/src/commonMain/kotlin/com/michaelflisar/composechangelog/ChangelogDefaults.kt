package com.michaelflisar.composechangelog

import com.michaelflisar.composechangelog.classes.ChangelogTextFormatter
import com.michaelflisar.composechangelog.format.ChangelogVersionFormatter
import com.michaelflisar.composechangelog.format.DefaultVersionFormatter

object ChangelogDefaults {

    fun setup(
        logFileReader: suspend () -> ByteArray,
        textFormatter: ChangelogTextFormatter = DefaultTextFormatter,
        versionFormatter: ChangelogVersionFormatter = DefaultVersionFormatter(),
        skipUnknownTags: Boolean = false,
        textMore: String = "More",
    ) = Changelog.Setup(
        logFileReader = logFileReader,
        textFormatter = textFormatter,
        versionFormatter = versionFormatter,
        skipUnknownTags = skipUnknownTags,
        textMore = textMore
    )

}

expect val ChangelogDefaults.DefaultTextFormatter: ChangelogTextFormatter