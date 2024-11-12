package com.michaelflisar.composechangelog

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItemRelease
import com.michaelflisar.composechangelog.composables.Changelog
import com.michaelflisar.composechangelog.internal.ChangelogParserUtil

actual typealias Context = android.content.Context
actual typealias ChangelogID = Int

@Composable
actual fun stringOk() = stringResource(android.R.string.ok)

@Composable
internal actual fun LocalContext(): Context {
    return androidx.compose.ui.platform.LocalContext.current
}

@Composable
internal actual fun ShowChangelogDialog(
    visible: MutableState<Boolean>,
    data: ChangelogData,
    setup: ChangelogSetup,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
            visible.value = false
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
                    visible.value = false
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

internal actual suspend fun ChangelogUtil.read(
    context: Context,
    changelogID: ChangelogID,
    versionFormatter: ChangelogVersionFormatter,
    sorter: Comparator<DataItemRelease>?
): ChangelogData {
    return ChangelogParserUtil.parse(context, changelogID, versionFormatter, sorter)
}