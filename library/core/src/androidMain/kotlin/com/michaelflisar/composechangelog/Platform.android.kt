package com.michaelflisar.composechangelog

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItemRelease
import com.michaelflisar.composechangelog.composables.Changelog
import com.michaelflisar.composechangelog.internal.ChangelogParserUtil

@Composable
actual fun stringOk() = stringResource(android.R.string.ok)

@Composable
internal actual fun ShowChangelogDialog(
    data: ChangelogData,
    setup: ChangelogSetup,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(text = setup.texts.dialogTitle)
        },
        text = {
            Changelog(data, setup)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }) {
                Text(setup.texts.dialogButtonDismiss)
            }
        }
    )
}

@Composable
internal actual fun LazyScrollContainer(state: LazyListState, content: LazyListScope.() -> Unit) {
    LazyColumn(state = state) {
        content()
    }
}

internal actual suspend fun ChangelogUtil.readFile(
    logFileReader: suspend () -> ByteArray,
    versionFormatter: ChangelogVersionFormatter,
    sorter: Comparator<DataItemRelease>?
): ChangelogData {
    return ChangelogParserUtil.parse(logFileReader, versionFormatter, sorter)
}