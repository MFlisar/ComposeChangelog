package com.michaelflisar.composechangelog

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.michaelflisar.composechangelog.core.R

fun ChangelogDefaults.setup(
    context: Context,
    changelogResourceId: Int = R.raw.changelog,
    textFormatter: @Composable (text: String) -> AnnotatedString = { it.toAnnotatedString() },
    versionFormatter: ChangelogVersionFormatter = DefaultVersionFormatter(),
    skipUnknownTags: Boolean = false,
    textMore: String = "More"
) = Changelog.Setup(
    logFileReader = {
        val resourceType: String = context.resources.getResourceTypeName(changelogResourceId)
        if (resourceType == "raw") {
            val inputStream = context.resources.openRawResource(changelogResourceId)
            val bytes = inputStream.readBytes()
            inputStream.close()
            bytes
        } else throw RuntimeException("Wrong changelog resource type, provide a raw resource!")
    },
    textFormatter = textFormatter,
    versionFormatter = versionFormatter,
    skipUnknownTags = skipUnknownTags,
    textMore = textMore
)


/**
 * returns the app version name
 *
 * @param context context to use to retrieve the app version name
 * @return the app version name
 */
fun Changelog.getAppVersionName(context: Context): String {
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
fun Changelog.getAppVersionCode(context: Context): Long {
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