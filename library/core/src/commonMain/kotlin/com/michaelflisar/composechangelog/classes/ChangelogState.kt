package com.michaelflisar.composechangelog.classes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.michaelflisar.composechangelog.ChangelogVersionFormatter
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver

class ChangelogState internal constructor(
    private val show: MutableState<Boolean>,
    internal var minimumVisibleReleaseVersion: MutableState<Long>,
) {

    val visible by show

    fun show(minimumVisibleReleaseVersion: Long = -1L) {
        this.minimumVisibleReleaseVersion.value = minimumVisibleReleaseVersion
        show.value = true
    }

    fun hide() {
        minimumVisibleReleaseVersion.value = -1L
        show.value = false
    }

    /**
     * checks if changelog should be shown by creating a [ShowChangelog]
     *
     * if last shown version is not the first app install AND if last version is less than current
     * then this will return a [ShowChangelog.Yes] (including the last shown version) to indicate that all changelog entries after the
     * last shown one need to be presented to the user
     *
     * @see [ShowChangelog]
     *
     * @return [ShowChangelog]
     */
    suspend fun checkShouldShowChangelogOnStart(
        stateSaver: IChangelogStateSaver,
        versionName: String,
        versionFormatter: ChangelogVersionFormatter,
    ) {
        val lastChangelog = stateSaver.lastShownVersion()
        val currentVersion = versionFormatter.parseVersion(versionName).toLong()
        val shouldShow = lastChangelog < currentVersion
        val isInitialVersion = lastChangelog <= 0L
        if (isInitialVersion) {
            stateSaver.saveLastShownVersion(currentVersion)
        } else if (shouldShow) {
            stateSaver.saveLastShownVersion(currentVersion)
            show(lastChangelog + 1)
        }
    }
}

@Composable
fun rememberChangelogState(
    visible: Boolean = false,
    minimumVisibleReleaseVersion: Long = -1L,
): ChangelogState {
    return ChangelogState(
        show = rememberSaveable { mutableStateOf(visible) },
        minimumVisibleReleaseVersion = rememberSaveable {
            mutableStateOf(
                minimumVisibleReleaseVersion
            )
        },
    )
}