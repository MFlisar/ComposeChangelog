package com.michaelflisar.composechangelog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.defaults.DefaultLazyScrollContainer
import com.michaelflisar.composechangelog.defaults.XMLRegexParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

actual val Changelog.IODispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

@Composable
internal actual fun String.toAnnotatedString(): AnnotatedString {
    return Util.toAnnotatedString(this)
}

@Composable
internal actual fun LazyScrollContainer(
    modifier: Modifier,
    state: LazyListState,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    content: LazyListScope.() -> Unit,
) {
    DefaultLazyScrollContainer(
        modifier = modifier,
        state = state,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

internal actual suspend fun Changelog.readFile(
    logFileReader: suspend () -> ByteArray,
    versionFormatter: ChangelogVersionFormatter,
): List<ChangelogReleaseItem> {
    return XMLRegexParser.parse(logFileReader, versionFormatter)
}