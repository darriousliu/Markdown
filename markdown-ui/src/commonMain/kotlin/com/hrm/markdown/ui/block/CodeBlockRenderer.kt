package com.hrm.markdown.ui.block

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.FencedCodeBlock
import com.hrm.markdown.parser.ast.IndentedCodeBlock
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * 围栏代码块渲染器 (``` 或 ~~~)
 *
 * 支持通过 info-string 的 `{...}` 属性语法控制：
 * - **title**: 标题栏，如 `{title="main.kt"}`
 * - **linenos / lineNumbers**: 行号显示
 * - **highlight / hl_lines**: 高亮指定行，如 `{highlight="2,5-7"}`
 */
@Composable
internal fun FencedCodeBlockRenderer(
    node: FencedCodeBlock,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    LocalMarkdownExtensionProvider.current.FencedCodeBlock(
        node = node,
        style = theme.codeBlock,
        modifier = modifier,
    )
}

/**
 * 缩进代码块渲染器
 */
@Composable
internal fun IndentedCodeBlockRenderer(
    node: IndentedCodeBlock,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    LocalMarkdownExtensionProvider.current.IndentedCodeBlock(
        node = node,
        style = theme.codeBlock,
        modifier = modifier,
    )
}
