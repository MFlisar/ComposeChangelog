package com.michaelflisar.composechangelog

import com.michaelflisar.composechangelog.DefaultVersionFormatter.Format.MajorMinor
import com.michaelflisar.composechangelog.DefaultVersionFormatter.Format.MajorMinorPatch
import com.michaelflisar.composechangelog.DefaultVersionFormatter.Format.MajorMinorPatchCandidate
import kotlin.math.floor
import kotlin.math.pow

class DefaultVersionFormatter(
    private val format: Format = MajorMinorPatch,
    private val prefix: String = "",
    private val suffix: String = ""
) : ChangelogVersionFormatter {

    enum class Format {

        /**
         * 10^2: Major Version
         * 10^0: Minor Version
         *
         * Examples:
         * - 1 => 0.1
         * - 10 => 0.10
         * - 100 => 1.0
         */
        MajorMinor,

        /**
         * 10^4: Major Version
         * 10^2: Minor Version
         * 10^0: Patch Version
         *
         * Examples:
         * - 1 => 0.0.1
         * - 10 => 0.0.10
         * - 100 => 0.1.0
         * - 1000 => 0.10.0
         * - 10000 => 1.0.0
         */
        MajorMinorPatch,

        /**
         * 10^6: Major Version
         * 10^4: Minor Version
         * 10^2: Patch Version
         * 10^0: Candidate Version (only displayed if != 0)
         *
         * Examples:
         * - 1 => 0.0.0-01
         * - 10 => 0.0.0-10
         * - 100 => 0.0.1
         * - 101 => 0.0.1-01
         * - 1000 => 0.0.10
         * - 10000 => 0.1.0
         * - 100000  => 0.10.0
         * - 1000000  => 1.0.0
         */
        MajorMinorPatchCandidate
    }

    override fun parseVersion(versionInfo: String): Int {
        val info = versionInfo.removePrefix(prefix).removeSuffix(suffix)
        val parts = info.split(".")
        val code = when (format) {
            MajorMinor -> {
                val major = parts[0].toInt()
                val minor = parts[1].toInt()
                major * 10f.pow(2) + minor
            }

            MajorMinorPatch -> {
                val major = parts[0].toInt()
                val minor = parts[1].toInt()
                val patch = parts[2].toInt()
                major * 10f.pow(4) + minor * 10f.pow(2) + patch
            }

            MajorMinorPatchCandidate -> {
                val major = parts[0].toInt()
                val minor = parts[1].toInt()
                var candidate = 0
                val patch: Int
                if (parts[2].contains("-")) {
                    val subParts = parts[2].split("-")
                    patch = subParts[0].toInt()
                    candidate = subParts[1].toInt()
                } else {
                    patch = parts[2].toInt()
                }
                major * 10f.pow(6) + minor * 10f.pow(4) + patch * 10f.pow(2) + candidate
            }
        }
        return code.toInt()
    }

    override fun formatVersion(versionCode: Int): String {
        val info = if (versionCode >= 0) {
            when (format) {
                MajorMinor -> {
                    val major = floor((versionCode.toFloat() / 100f)).toInt()
                    val minor = versionCode - major * 100
                    "$major.$minor"
                }

                MajorMinorPatch -> {
                    var tmp = versionCode.toFloat()
                    val major = floor((tmp / 10f.pow(4))).toInt()
                    tmp -= major * 10f.pow(4)
                    val minor = floor((tmp / 10f.pow(2))).toInt()
                    tmp -= minor * 10f.pow(2)
                    val patch = tmp.toInt()
                    "$major.$minor.$patch"
                }

                MajorMinorPatchCandidate -> {
                    var tmp = versionCode.toFloat()
                    val major = floor((tmp / 10f.pow(6))).toInt()
                    tmp -= major * 10f.pow(6)
                    val minor = floor((tmp / 10f.pow(4))).toInt()
                    tmp -= minor * 10f.pow(4)
                    val patch = floor((tmp / 10f.pow(2))).toInt()
                    tmp -= patch * 10f.pow(2)
                    val candidate = tmp.toInt()
                    val version = "$major.$minor.$patch"
                    if (candidate == 0) {
                        version
                    } else {
                        val candidateString = if (candidate < 10) "0$candidate" else candidate.toString()
                        "%version-$candidateString"
                    }
                }
            }
        } else throw RuntimeException("Invalid version code!")
        return "$prefix$info$suffix"
    }
}