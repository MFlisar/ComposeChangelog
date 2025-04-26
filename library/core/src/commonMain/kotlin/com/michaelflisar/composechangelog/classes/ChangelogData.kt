package com.michaelflisar.composechangelog.classes

import com.michaelflisar.composechangelog.data.ChangelogReleaseItem

sealed class ChangelogData {

    data object Loading : ChangelogData()

    data class Data(
        val items: List<ChangelogReleaseItem>,
    ) : ChangelogData()
}