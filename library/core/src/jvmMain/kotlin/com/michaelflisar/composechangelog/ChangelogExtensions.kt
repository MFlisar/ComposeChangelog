package com.michaelflisar.composechangelog

import com.michaelflisar.composechangelog.classes.ChangelogTextFormatter
import com.michaelflisar.composechangelog.format.ChangelogVersionFormatter
import com.michaelflisar.composechangelog.format.DefaultVersionFormatter
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun ChangelogDefaults.setup(
    file: File = File("changelog.xml"),
    textFormatter: ChangelogTextFormatter = DefaultTextFormatter,
    versionFormatter: ChangelogVersionFormatter = DefaultVersionFormatter(),
    skipUnknownTags: Boolean = false,
    textMore: String = "More",
) = Changelog.Setup(
    logFileReader = { file.readBytes() },
    textFormatter = textFormatter,
    versionFormatter = versionFormatter,
    skipUnknownTags = skipUnknownTags,
    textMore = textMore
)

/**
 * returns the app version name (the packageVersion from the exe file metadata)
 *
 * @param exe the exe to read the file version from
 *
 * @return the app version name
 */
fun Changelog.getAppVersionName(): String {
    val exePath =
        File(Changelog::class.java.protectionDomain.codeSource.location.toURI()).absolutePath
    val versionInfo = runPS("(Get-Item '${exePath}').VersionInfo.FileVersion")
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
    } catch (e: Exception) {
        return ""
    }
}