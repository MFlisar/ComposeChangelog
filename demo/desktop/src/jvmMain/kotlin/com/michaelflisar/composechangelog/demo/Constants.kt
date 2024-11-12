package com.michaelflisar.composechangelog.demo

import com.michaelflisar.composechangelog.DefaultVersionFormatter

object Constants {
    val CHANGELOG_FORMATTER =
        DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatchCandidate)
}