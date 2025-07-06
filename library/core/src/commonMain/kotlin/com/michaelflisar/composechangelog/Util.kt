package com.michaelflisar.composechangelog

import androidx.compose.ui.text.AnnotatedString

internal object Util {

    fun toAnnotatedString(text: String): AnnotatedString {
        val cleanText = text.replace(Regex("<[^>]*>"), "")
        return AnnotatedString(cleanText)
    }
}