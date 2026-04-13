package com.hrm.markdown.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

typealias MarkdownTheme = com.hrm.markdown.ui.theme.MarkdownTheme
typealias AdmonitionStyle = com.hrm.markdown.ui.theme.AdmonitionStyle

internal val LocalMarkdownTheme = compositionLocalOf { MarkdownTheme() }

/**
 * 提供 Markdown 主题到组件树。
 */
@Composable
internal fun ProvideMarkdownTheme(
    theme: MarkdownTheme = MarkdownTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalMarkdownTheme provides theme) {
        content()
    }
}
