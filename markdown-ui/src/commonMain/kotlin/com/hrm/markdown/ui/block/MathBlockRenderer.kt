package com.hrm.markdown.ui.block

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.MathBlock
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * 数学公式块渲染器 ($$...$$)
 * 使用 LaTeX 库渲染数学公式，通过 LatexMeasurer 预测量精确尺寸，避免多余空白。
 *
 * 公式编号（`\tag{N}`）、环境自动编号（equation/align 等）、引用（`\ref`/`\eqref`）
 * 均由 LaTeX 渲染库原生处理，无需额外的编号展示逻辑。
 */
@Composable
internal fun MathBlockRenderer(
    node: MathBlock,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    LocalMarkdownExtensionProvider.current.MathBlock(
        node = node,
        style = theme.math,
        modifier = modifier,
    )
}
