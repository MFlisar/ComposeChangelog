package com.michaelflisar.composechangelog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import com.michaelflisar.composechangelog.classes.ChangelogTextFormatter

actual val ChangelogDefaults.DefaultTextFormatter: ChangelogTextFormatter
    get() = object: ChangelogTextFormatter {
        @Composable
        override fun format(text: String): AnnotatedString {
            return htmlToAnnotatedString(text)
        }
    }