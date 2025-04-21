package com.michaelflisar.composechangelog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.michaelflisar.composechangelog.classes.DataItem
import com.michaelflisar.composechangelog.interfaces.IChangelogFilter
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@Composable
fun ChangelogDefaults.setup(
    file: File = File("changelog.xml"),
    texts: ChangelogSetup.Texts = ChangelogDefaults.texts(),
    useShowMoreButtons: Boolean = true,
    versionFormatter: ChangelogVersionFormatter = DefaultVersionFormatter(),
    sorter: ((items: List<DataItem>) -> List<DataItem>)? = ChangelogDefaults.sorter(),
    filter: IChangelogFilter? = null,
    renderer: ChangelogSetup.Renderer = ChangelogDefaults.renderer()
) = ChangelogSetup(
    logFileReader = { file.readBytes() },
    texts = texts,
    useShowMoreButtons = useShowMoreButtons,
    versionFormatter = versionFormatter,
    filter = filter,
    sorter = sorter,
    renderer = renderer
)

/**
 * returns the app version name (the packageVersion from the exe file metadata)
 *
 * @param exe the exe to read the file version from
 *
 * @return the app version name
 */
fun ChangelogUtil.getAppVersionName(
    exe: File = File(System.getProperty("user.dir")).let {
        File(it, it.name + ".exe")
    }
): String {
    val versionInfo = runPS("(Get-Item '${exe.absolutePath}').VersionInfo.FileVersion")
    return versionInfo.takeIf { it.isNotEmpty() } ?: "<UNKNOWN>"
}

fun runPS(command: String): String {
    try {
        @Suppress("DEPRECATION")
        val powerShellProcess = Runtime.getRuntime().exec("powershell.exe $command")
        powerShellProcess.outputStream.close()

        val lines = ArrayList<String>()

        var line: String?
        //println("Standard Output:")
        val stdout = BufferedReader(InputStreamReader(powerShellProcess.inputStream))
        while ((stdout.readLine().also { line = it }) != null) {
            line?.let { lines += it }
        }
        stdout.close()
        //println("Standard Error:")
        val stderr = BufferedReader(InputStreamReader(powerShellProcess.errorStream))
        while ((stderr.readLine().also { line = it }) != null) {
            println(line)
        }
        stderr.close()
        //println("Done")

        return lines.firstOrNull() ?: ""
    } catch(e: Exception) {
        return ""
    }
}