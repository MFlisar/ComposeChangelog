package com.michaelflisar.composechangelog

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import com.michaelflisar.composechangelog.classes.DataItem
import com.michaelflisar.composechangelog.interfaces.IChangelogFilter

@Composable
fun ChangelogDefaults.setup(
    changelogResourceId: Int = R.raw.changelog,
    texts: ChangelogSetup.Texts = texts(),
    useShowMoreButtons: Boolean = true,
    versionFormatter: ChangelogVersionFormatter = DefaultVersionFormatter(),
    sorter: ((items: List<DataItem>) -> List<DataItem>)? = ChangelogDefaults.sorter(),
    filter: IChangelogFilter? = null,
    renderer: ChangelogSetup.Renderer = renderer()
) = ChangelogSetup(
    changelogId = changelogResourceId,
    texts = texts,
    useShowMoreButtons = useShowMoreButtons,
    versionFormatter = versionFormatter,
    filter = filter,
    sorter = sorter,
    renderer = renderer
)


/**
 * returns the app version name
 *
 * @param context context to use to retrieve the app version name
 * @return the app version name
 */
fun ChangelogUtil.getAppVersionName(context: Context): String {
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
fun ChangelogUtil.getAppVersionCode(context: Context, versionFormatter: ChangelogVersionFormatter): Long {
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