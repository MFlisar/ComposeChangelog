package com.michaelflisar.composechangelog

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItemRelease
import com.michaelflisar.composechangelog.composables.Changelog
import com.michaelflisar.composechangelog.internal.ChangelogParserUtil

@Composable
actual fun stringOk() = "OK"

@Composable
internal actual fun ShowChangelogDialog(
    data: ChangelogData,
    setup: ChangelogSetup,
    onDismiss: () -> Unit
) {
    DialogWindow(
        visible = true,
        title = setup.texts.dialogTitle,
        onCloseRequest = {
            onDismiss()
        },
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
            width = 600.dp,
            height = 400.dp
        )
    ) {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Changelog(data, setup)
            }
        }
    }
}

@Composable
internal actual fun LazyScrollContainer(state: LazyListState, content: LazyListScope.() -> Unit) {
    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp),
            state = state
        ) {
            content()
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(state)
        )
    }
}

internal actual suspend fun ChangelogUtil.readFile(
    logFileReader: suspend () -> ByteArray,
    versionFormatter: ChangelogVersionFormatter,
    sorter: Comparator<DataItemRelease>?
): ChangelogData {
    return ChangelogParserUtil.parse(logFileReader, versionFormatter, sorter)
}