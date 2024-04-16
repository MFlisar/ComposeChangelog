package com.michaelflisar.composechangelog

interface ChangelogVersionFormatter {
    fun parseVersion(versionInfo: String): Int
    fun formatVersion(versionCode: Int): String
}