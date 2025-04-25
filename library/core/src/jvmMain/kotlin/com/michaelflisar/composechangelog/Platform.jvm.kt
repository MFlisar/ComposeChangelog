package com.michaelflisar.composechangelog

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import com.michaelflisar.composechangelog.classes.ChangelogSetup
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.data.XMLTag
import com.michaelflisar.composechangelog.internal.ChangelogParserUtil
import com.michaelflisar.composechangelog.composables.Changelog

@Composable
internal actual fun String.toAnnotatedString(): AnnotatedString {
    val cleanText = this.replace(Regex("<[^>]*>"), "")
    return AnnotatedString(cleanText)
}

@Composable
internal actual fun LazyScrollContainer(
    state: LazyListState,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    content: LazyListScope.() -> Unit
) {
    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
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
    versionFormatter: ChangelogVersionFormatter
): List<ChangelogReleaseItem> {
    return ChangelogParserUtil.parse(logFileReader, versionFormatter)
}

internal actual fun XMLTag.children(): List<XMLTag> {
    return ChangelogParserUtil.children(this)
}