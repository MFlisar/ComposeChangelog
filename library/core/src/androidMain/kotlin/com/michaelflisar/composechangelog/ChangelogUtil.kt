package com.michaelflisar.composechangelog

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.michaelflisar.composechangelog.classes.ShowChangelog
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver


object ChangelogUtil {

    /**
     * returns the app version name
     *
     * @param context context to use to retrieve the app version name
     * @return the app version name
     */
    fun getAppVersionName(context: Context): String {
        try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            return info.versionName ?: "<NULL>"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "<UNKNOWN>"
    }

    /**
     * returns the app version code
     *
     * @param context context to use to retrieve the app version code
     * @return the app version code
     */
    @Suppress("DEPRECATION")
    fun getAppVersionCode(context: Context): Long {
        try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                info.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return -1
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
    suspend fun shouldShowChangelogOnStart(
        context: Context,
        storage: IChangelogStateSaver
    ): ShowChangelog {
        val lastChangelog = storage.lastShownVersion()
        val currentVersion = getAppVersionCode(context)
        return ShowChangelog(lastChangelog, currentVersion)
    }
}