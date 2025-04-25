package com.michaelflisar.composechangelog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import com.michaelflisar.composechangelog.classes.ChangelogSetup
import com.michaelflisar.composechangelog.composables.Changelog
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.data.XMLTag
import com.michaelflisar.composechangelog.internal.ChangelogParserUtil

@Composable
internal actual fun String.toAnnotatedString(): AnnotatedString = AnnotatedString.fromHtml(this)

@Composable
internal actual fun LazyScrollContainer(
    state: LazyListState,
    verticalArrangement: Arrangement.Vertical,
    horizontalAlignment: Alignment.Horizontal,
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        state = state,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        content()
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