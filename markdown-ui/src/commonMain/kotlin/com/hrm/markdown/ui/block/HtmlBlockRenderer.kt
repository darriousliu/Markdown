package com.hrm.markdown.ui.block

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.HtmlBlock
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * HTML 块渲染器：以等宽字体显示原始 HTML 文本，不做 HTML 渲染。
 *
 * 如果业务需要实际渲染 HTML，可在外部通过 [com.hrm.markdown.ui.extension.MarkdownExtensionProvider]
 * 的 `CustomContainer` 或其他定制路径接管。
 */
@Composable
internal fun HtmlBlockRenderer(node: HtmlBlock, modifier: Modifier = Modifier) {
    val theme = LocalMarkdownTheme.current
    BasicText(
        text = node.literal.trimEnd('\n'),
        modifier = modifier,
        style = theme.htmlBlock.textStyle,
    )
}
