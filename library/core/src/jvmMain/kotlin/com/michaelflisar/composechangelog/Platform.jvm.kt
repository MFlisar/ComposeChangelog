package com.michaelflisar.composechangelog

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.internal.ChangelogParserUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val Changelog.IODispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

@Composable
internal actual fun String.toAnnotatedString(): AnnotatedString {
    val cleanText = this.replace(Regex("<[^>]*>"), "")
    return AnnotatedString(cleanText)
}

@Composable
internal actual fun LazyScrollContainer(
    modifier: Modifier,
    state: LazyListState,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    content: LazyListScope.() -> Unit,
) {
    Box {
        LazyColumn(
            modifier = modifier.padding(end = 16.dp),
            state = state,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        ) {
            content()
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(state)
        )
    }
}

internal actual suspend fun Changelog.readFile(
    logFileReader: suspend () -> ByteArray,
    versionFormatter: ChangelogVersionFormatter,
): List<ChangelogReleaseItem> {
    return ChangelogParserUtil.parse(logFileReader, versionFormatter)
}