package com.hrm.markdown.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalMarkdownElementTheme = compositionLocalOf { MarkdownElementTheme() }
val LocalMarkdownExtensionProvider = compositionLocalOf<MarkdownExtensionProvider> { MarkdownExtensionProvider.None }

@Composable
fun ProvideMarkdownUiContract(
    theme: MarkdownElementTheme = MarkdownElementTheme(),
    extensionProvider: MarkdownExtensionProvider = MarkdownExtensionProvider.None,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalMarkdownElementTheme provides theme,
        LocalMarkdownExtensionProvider provides extensionProvider,
    ) {
        content()
    }
}
