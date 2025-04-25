package com.michaelflisar.composechangelog.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState

@Composable
fun Dialog(
    visible: Boolean,
    title: String,
    onCloseRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    DialogWindow(
        visible = visible,
        title = title,
        onCloseRequest = onCloseRequest,
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
                content()
            }
        }
    }
}