package com.michaelflisar.composechangelog.interfaces

import androidx.compose.runtime.Composable
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem

interface IChangelogReleaseRenderer {
    @Composable
    fun render(release: ChangelogReleaseItem)
}