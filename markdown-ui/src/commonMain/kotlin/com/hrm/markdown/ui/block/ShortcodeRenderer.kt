package com.hrm.markdown.ui.block

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.ShortcodeBlock
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.MarkdownBlockChildren
import com.hrm.markdown.ui.theme.LocalMarkdownTheme

/**
 * 块级短代码渲染器：`{% tag args %}...{% endtag %}`。
 *
 * 显示短代码标签名和参数，如果有子内容则渲染子块。
 */
@Composable
internal fun ShortcodeBlockRenderer(
    node: ShortcodeBlock,
    modifier: Modifier = Modifier,
) {
    LocalMarkdownExtensionProvider.current.ShortcodeBlock(
        node = node,
        theme = LocalMarkdownTheme.current,
        modifier = modifier,
    ) {
        MarkdownBlockChildren(parent = node)
    }
}
