package com.michaelflisar.composechangelog.classes

import android.os.Parcelable
import androidx.compose.runtime.saveable.Saver
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * checks if changelog should be shown or not
 *
 * if last shown version is not the first app install AND if last version is less than current
 * then [ShowChangelog.shouldShow] is true, otherwise it is false
 */
@Parcelize
data class ShowChangelog(
    val lastShownVersion: Long,
    val currentVersion: Long
) : Parcelable {
    @IgnoredOnParcel
    val shouldShow = lastShownVersion != -1L && lastShownVersion < currentVersion
}