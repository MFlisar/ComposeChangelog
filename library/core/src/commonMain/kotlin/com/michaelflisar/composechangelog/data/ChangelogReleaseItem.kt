package com.michaelflisar.composechangelog.data

class ChangelogReleaseItem(
    val versionCode: Int,
    val versionName: String,
    val date: String,
    val title: String?,
    val items: List<XMLTag>,
)