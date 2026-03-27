package com.michaelflisar.composechangelog.classes

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString

interface ChangelogTextFormatter {

    @Composable
    fun format(text: String): AnnotatedString

}