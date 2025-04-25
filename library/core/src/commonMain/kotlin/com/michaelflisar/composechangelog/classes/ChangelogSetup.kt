package com.michaelflisar.composechangelog.classes

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.michaelflisar.composechangelog.ChangelogVersionFormatter

data class ChangelogSetup internal constructor(
    val logFileReader: suspend () -> ByteArray,
    val textFormatter: @Composable (text: String) -> AnnotatedString,
    val versionFormatter: ChangelogVersionFormatter,
    val skipUnknownTags: Boolean,
    val textMore: String
)