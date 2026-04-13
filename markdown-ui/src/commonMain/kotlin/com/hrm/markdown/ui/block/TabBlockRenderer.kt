package com.hrm.markdown.ui.block

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.TabBlock
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * 内容标签页渲染器（MkDocs Material 风格）。
 */
@Composable
internal fun TabBlockRenderer(
    node: TabBlock,
    modifier: Modifier = Modifier,
) {
    LocalMarkdownExtensionProvider.current.TabBlock(
        node = node,
        theme = LocalMarkdownTheme.current,
        modifier = modifier,
    )
}
