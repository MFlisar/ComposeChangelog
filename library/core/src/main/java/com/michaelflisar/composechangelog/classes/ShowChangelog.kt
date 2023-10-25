package com.michaelflisar.composechangelog.classes

/**
 * checks if changelog should be shown or not
 *
 * if last shown version is not the first app install AND if last version is less than current
 * then [ShowChangelog.shouldShow] is true, otherwise it is false
 */
data class ShowChangelog(
    val lastShownVersion: Long,
    val currentVersion: Long
) {
    val shouldShow = lastShownVersion != -1L && lastShownVersion < currentVersion
}