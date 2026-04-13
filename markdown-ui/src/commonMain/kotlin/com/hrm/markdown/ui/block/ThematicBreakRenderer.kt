package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * 水平分割线渲染器（`---`、`***`、`___`）。
 *
 * 直接使用 [Spacer] + [Modifier.drawBehind] 绘制，不依赖 Material3 HorizontalDivider。
 * 外部 [Modifier] 来自 [com.hrm.markdown.ui.theme.MarkdownElementModifiers.thematicBreak]。
 */
@Composable
internal fun ThematicBreakRenderer(modifier: Modifier = Modifier) {
    val theme = LocalMarkdownTheme.current
    val thickness = theme.thematicBreak.thickness
    val color = theme.thematicBreak.color

    Spacer(
        modifier = modifier
            .height(thickness)
            .drawBehind {
                val t = thickness.toPx()
                val y = size.height / 2
                drawLine(
                    color = color,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = t,
                )
            },
    )
}
