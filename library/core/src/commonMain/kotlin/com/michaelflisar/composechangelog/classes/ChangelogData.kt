package com.michaelflisar.composechangelog.classes

class ChangelogData(
    val releases: List<DataItemRelease>
) {
    fun isEmpty() = releases.sumOf { it.items.size } == 0
}