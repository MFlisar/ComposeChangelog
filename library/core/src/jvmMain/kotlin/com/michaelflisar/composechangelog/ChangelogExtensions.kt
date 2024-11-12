package com.michaelflisar.composechangelog

import androidx.compose.runtime.Composable
import com.michaelflisar.composechangelog.ChangelogDefaults.renderer
import com.michaelflisar.composechangelog.ChangelogDefaults.texts
import com.michaelflisar.composechangelog.classes.DataItem
import com.michaelflisar.composechangelog.interfaces.IChangelogFilter
import java.io.File

@Composable
fun ChangelogDefaults.setup(
    file: File = File("changelog.xml"),
    texts: ChangelogSetup.Texts = texts(),
    useShowMoreButtons: Boolean = true,
    versionFormatter: ChangelogVersionFormatter = DefaultVersionFormatter(),
    sorter: ((items: List<DataItem>) -> List<DataItem>)? = ChangelogDefaults.sorter(),
    filter: IChangelogFilter? = null,
    renderer: ChangelogSetup.Renderer = renderer()
) = ChangelogSetup(
    changelogId = file,
    texts = texts,
    useShowMoreButtons = useShowMoreButtons,
    versionFormatter = versionFormatter,
    filter = filter,
    sorter = sorter,
    renderer = renderer
)