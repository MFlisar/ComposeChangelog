package com.michaelflisar.composechangelog.gradle.plugin

import com.michaelflisar.composechangelog.ChangelogVersionFormatter
import kotlin.math.ceil
import kotlin.math.floor

object ComposeChangelog {

    /**
     * uses the formatter to parse the versionInfo to a valid version code and logs the result
     * in the build window
     *
     * @return version code
     */
    fun buildVersionCode(
        versionInfo: String,
        formatter: ChangelogVersionFormatter,
        logInfo: Boolean = true
    ): Int {

        val code = formatter.parseVersion(versionInfo)

        val info1 = "$versionInfo => $code"
        val t1 = (floor((28.0 - info1.length) / 2.0)).toInt().coerceAtLeast(0)
        val t2 = (ceil((28.0 - info1.length) / 2.0)).toInt().coerceAtLeast(0)
        val info2 = " ".repeat(t1) + info1 + " ".repeat(t2)

        if (logInfo) {
            println("")
            println("##############################")
            println("#     BUILD VERSION CODE     #")
            println("# -------------------------- #")
            println("#$info2#")
            println("##############################")
            println("")
        }

        return code
    }
}