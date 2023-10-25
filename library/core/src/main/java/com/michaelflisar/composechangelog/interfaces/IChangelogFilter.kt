package com.michaelflisar.composechangelog.interfaces

import com.michaelflisar.composechangelog.classes.DataItemRelease
import com.michaelflisar.composechangelog.classes.DataItem

interface IChangelogFilter {
    fun keep(release: DataItemRelease): Boolean
    fun keep(release: DataItemRelease, item: DataItem): Boolean
}