package com.michaelflisar.composechangelog.classes

data class DataItem(
    override val id: Int,
    val tag: String,
    val text: String,
    override val filter: String?,
    val isSummary: Boolean
) : IChangelogData