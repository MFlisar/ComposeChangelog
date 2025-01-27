package com.michaelflisar.composechangelog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.michaelflisar.composechangelog.classes.DataItem
import com.michaelflisar.composechangelog.classes.DataItemRelease
import com.michaelflisar.composechangelog.interfaces.IChangelogFilter

data class ChangelogSetup internal constructor(
    val logFileReader: suspend () -> ByteArray,
    val texts: Texts,
    val useShowMoreButtons: Boolean,
    val versionFormatter: ChangelogVersionFormatter,
    val filter: IChangelogFilter? = null,
    val sorter: ((items: List<DataItem>) -> List<DataItem>)?,
    val renderer: Renderer
) {
    data class Texts(
        val dialogTitle: String,
        val dialogButtonDismiss: String,
        val buttonShowMore: String
    )

    data class Renderer internal constructor(
        val itemRelease: @Composable (modifier: Modifier, item: DataItemRelease, setup: ChangelogSetup) -> Unit,
        val item: @Composable (modifier: Modifier, item: DataItem, setup: ChangelogSetup) -> Unit,
        val itemShowMore: @Composable (modifier: Modifier, setup: ChangelogSetup, onClick: () -> Unit) -> Unit
    )
}