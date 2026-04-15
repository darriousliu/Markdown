package com.hrm.markdown.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import com.hrm.markdown.parser.ast.Admonition
import com.hrm.markdown.ui.theme.LocalMarkdownTheme
import com.hrm.markdown.ui.MarkdownBlockChildren
import com.hrm.markdown.ui.theme.ProvideMarkdownTheme

/**
 * Admonition 渲染器 (> [!NOTE], > [!WARNING] 等)。
 */
@Composable
internal fun AdmonitionRenderer(
    node: Admonition,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val layoutDirection = LocalLayoutDirection.current
    val style = theme.admonition.resolve(node.type)
    val contentPadding = theme.admonitionPadding
    val contentTextStyle = theme.paragraph.textStyle.merge(
        theme.admonitionContentTextStyle ?: TextStyle()
    )
    val contentTheme = theme.copy(
        paragraph = theme.paragraph.copy(textStyle = contentTextStyle),
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(theme.admonitionCornerRadius))
            .background(style.background)
            .drawBehind {
                drawLine(
                    color = style.borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = theme.admonitionBorderWidth.toPx(),
                )
            }
            .padding(
                PaddingValues(
                    start = contentPadding.calculateLeftPadding(layoutDirection) + theme.admonitionBorderWidth,
                    top = contentPadding.calculateTopPadding(),
                    end = contentPadding.calculateRightPadding(layoutDirection),
                    bottom = contentPadding.calculateBottomPadding(),
                )
            ),
    ) {
        // 标题行
        Row {
            BasicText(
                text = style.iconText,
                modifier = Modifier.padding(end = theme.admonitionIconSpacing),
            )
            BasicText(
                text = node.title.ifEmpty { node.type.uppercase() },
                style = theme.admonitionTitleTextStyle.copy(color = style.titleColor),
            )
        }

        // 内容
        if (node.children.isNotEmpty()) {
            ProvideMarkdownTheme(contentTheme) {
                MarkdownBlockChildren(
                    parent = node,
                    modifier = Modifier.padding(top = theme.admonitionTitleContentSpacing),
                )
            }
        }
    }
}
