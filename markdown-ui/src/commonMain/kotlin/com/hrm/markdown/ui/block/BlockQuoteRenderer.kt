package com.hrm.markdown.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import com.hrm.markdown.parser.ast.BlockQuote
import com.hrm.markdown.ui.LocalMarkdownTheme
import com.hrm.markdown.ui.MarkdownBlockChildren
import com.hrm.markdown.ui.ProvideMarkdownTheme
import androidx.compose.ui.text.TextStyle

/**
 * 块引用渲染器 (> ...)
 * 左侧绘制竖线，内部递归渲染子块。
 */
@Composable
internal fun BlockQuoteRenderer(
    node: BlockQuote,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val borderColor = theme.blockQuoteBorderColor
    val borderWidthPx = theme.blockQuoteBorderWidth
    val quotedTextStyle = theme.bodyStyle.merge(
        theme.blockQuoteTextStyle ?: TextStyle(color = theme.blockQuoteTextColor)
    ).let { merged ->
        if (merged.color == Color.Unspecified) merged.copy(color = theme.blockQuoteTextColor) else merged
    }
    val quotedTheme = theme.copy(
        paragraph = theme.paragraph.copy(textStyle = quotedTextStyle),
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(theme.blockQuoteCornerRadius))
            .background(theme.blockQuoteBackground)
            .drawBehind {
                val strokeWidth = borderWidthPx.toPx()
                drawLine(
                    color = borderColor,
                    start = Offset(strokeWidth / 2, 0f),
                    end = Offset(strokeWidth / 2, size.height),
                    strokeWidth = strokeWidth,
                )
            }
            .padding(
                start = theme.blockQuotePadding + theme.blockQuoteBorderWidth,
                top = theme.blockQuotePadding,
                end = theme.blockQuotePadding,
                bottom = theme.blockQuotePadding,
            ),
    ) {
        ProvideMarkdownTheme(quotedTheme) {
            MarkdownBlockChildren(node)
        }
    }
}
