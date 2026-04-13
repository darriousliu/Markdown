package com.hrm.markdown.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.hrm.markdown.parser.ast.Admonition
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * Admonition 渲染器（`> [!NOTE]`、`> [!WARNING]` 等）。
 *
 * 不依赖 Material3，使用 [Modifier.drawBehind] 绘制左侧色条。
 */
@Composable
internal fun AdmonitionRenderer(node: Admonition, modifier: Modifier = Modifier) {
    val theme = LocalMarkdownTheme.current
    val styleSet = theme.admonition
    val style = styleSet.resolve(node.type)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(style.backgroundColor)
            .drawBehind {
                val stroke = styleSet.borderWidth.toPx()
                drawLine(
                    color = style.borderColor,
                    start = Offset(stroke / 2, 0f),
                    end = Offset(stroke / 2, size.height),
                    strokeWidth = stroke,
                )
            }
            .padding(
                start = styleSet.padding + styleSet.borderWidth,
                top = styleSet.padding,
                end = styleSet.padding,
                bottom = styleSet.padding,
            ),
    ) {
        Row {
            BasicText(
                text = style.iconText,
                modifier = Modifier.padding(end = 8.dp),
            )
            BasicText(
                text = node.title.ifEmpty { node.type.uppercase() },
                style = styleSet.titleTextStyle.copy(color = style.titleColor),
            )
        }
        if (node.children.isNotEmpty()) {
            MarkdownBlockChildren(
                parent = node,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
