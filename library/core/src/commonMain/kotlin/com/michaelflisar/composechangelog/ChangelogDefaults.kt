package com.michaelflisar.composechangelog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.michaelflisar.composechangelog.core.resources.Res

object ChangelogDefaults {

    fun setup(
        logFileReader: suspend () -> ByteArray,
        textFormatter: @Composable (text: String) -> AnnotatedString = { it.toAnnotatedString() },
        versionFormatter: ChangelogVersionFormatter = DefaultVersionFormatter(),
        skipUnknownTags: Boolean = false,
        textMore: String = "More"
    ) = Changelog.Setup(
        logFileReader = logFileReader,
        textFormatter = textFormatter,
        versionFormatter = versionFormatter,
        skipUnknownTags = skipUnknownTags,
        textMore = textMore
    )

}