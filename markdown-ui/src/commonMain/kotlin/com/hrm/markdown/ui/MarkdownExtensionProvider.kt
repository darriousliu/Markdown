package com.hrm.markdown.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.CustomContainer
import com.hrm.markdown.parser.ast.DiagramBlock
import com.hrm.markdown.parser.ast.MathBlock
import com.hrm.markdown.parser.ast.ShortcodeBlock

/**
 * Flavor 扩展节点 Provider：由外部实现具体 UI。
 */
interface MarkdownExtensionProvider {
    @Composable
    fun Math(
        node: MathBlock,
        style: MathTheme,
        modifier: Modifier,
    ): Boolean = false

    @Composable
    fun Diagram(
        node: DiagramBlock,
        style: CodeBlockTheme,
        modifier: Modifier,
    ): Boolean = false

    @Composable
    fun CustomContainer(
        node: CustomContainer,
        style: MarkdownElementTheme,
        modifier: Modifier,
    ): Boolean = false

    @Composable
    fun Shortcode(
        node: ShortcodeBlock,
        style: MarkdownElementTheme,
        modifier: Modifier,
    ): Boolean = false

    object None : MarkdownExtensionProvider
}
