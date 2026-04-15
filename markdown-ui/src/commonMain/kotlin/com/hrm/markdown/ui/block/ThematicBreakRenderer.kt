package com.hrm.markdown.ui.block

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.theme.LocalMarkdownTheme

/**
 * 水平分割线渲染器 (---, ***, ___)
 */
@Composable
internal fun ThematicBreakRenderer(
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    LocalMarkdownExtensionProvider.current.HorizontalDivider(
        style = theme.thematicBreak,
        modifier = modifier,
    )
}
