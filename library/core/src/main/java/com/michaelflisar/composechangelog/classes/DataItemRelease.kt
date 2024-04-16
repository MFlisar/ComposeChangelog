package com.michaelflisar.composechangelog.classes

data class DataItemRelease(
    override val id: Int,
    val versionCode: Int,
    val versionInfo: String,
    val date: String,
    override val filter: String?,
    val items: List<DataItem>
) : IChangelogData {

    fun getSummaryItems() = items.filter { it.isSummary }

    fun getNonSummaryItems() = items.filter { !it.isSummary }
}