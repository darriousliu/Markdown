package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.hrm.markdown.parser.ast.BlockQuote
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * 块引用渲染器（`> ...`）。
 *
 * 通过 [Modifier.drawBehind] 在左侧绘制竖线，避免引入 Material3 的 Divider。
 */
@Composable
internal fun BlockQuoteRenderer(node: BlockQuote, modifier: Modifier = Modifier) {
    val theme = LocalMarkdownTheme.current
    val borderColor = theme.blockQuote.borderColor
    val borderWidth = theme.blockQuote.borderWidth
    val contentPadding = theme.blockQuote.contentPadding

    Box(
        modifier = modifier
            .drawBehind {
                val stroke = borderWidth.toPx()
                drawLine(
                    color = borderColor,
                    start = Offset(stroke / 2, 0f),
                    end = Offset(stroke / 2, size.height),
                    strokeWidth = stroke,
                )
            }
            .padding(start = contentPadding + borderWidth),
    ) {
        MarkdownBlockChildren(node)
    }
}
