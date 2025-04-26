package com.michaelflisar.composechangelog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.data.XMLTag

@Composable
internal expect fun String.toAnnotatedString(): AnnotatedString

@Composable
internal expect fun LazyScrollContainer(
    modifier: Modifier,
    state: LazyListState,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    content: LazyListScope.() -> Unit,
)

internal expect suspend fun Changelog.readFile(
    logFileReader: suspend () -> ByteArray,
    versionFormatter: ChangelogVersionFormatter,
): List<ChangelogReleaseItem>

internal expect fun XMLTag.children(): List<XMLTag>

