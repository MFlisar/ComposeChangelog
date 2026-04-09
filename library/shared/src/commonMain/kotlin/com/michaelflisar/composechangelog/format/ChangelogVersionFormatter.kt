package com.michaelflisar.composechangelog.format

interface ChangelogVersionFormatter {
    fun parseVersion(versionInfo: String): Int
    fun formatVersion(versionCode: Int): String
}