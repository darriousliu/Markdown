package com.hrm.markdown.ui.extension

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hrm.markdown.parser.ast.CustomContainer
import com.hrm.markdown.parser.ast.DiagramBlock
import com.hrm.markdown.parser.ast.Figure
import com.hrm.markdown.parser.ast.Image
import com.hrm.markdown.parser.ast.MathBlock
import com.hrm.markdown.parser.ast.ShortcodeBlock
import com.hrm.markdown.parser.ast.TabBlock
import com.hrm.markdown.parser.ast.TabItem
import com.hrm.markdown.ui.theme.CodeBlockStyle
import com.hrm.markdown.ui.theme.FigureStyle
import com.hrm.markdown.ui.theme.ImageStyle
import com.hrm.markdown.ui.theme.MarkdownTheme
import com.hrm.markdown.ui.theme.MathStyle

/**
 * 扩展节点的默认降级实现。
 *
 * 当调用方未提供 [MarkdownExtensionProvider] 的对应实现时，renderer 会走这里，
 * 以「原样文本 + 最小视觉结构」的方式保证文档完整显示，
 * 同时不引入任何 UI 框架（Material3 等）的依赖。
 */
internal object DefaultExtensionFallbacks {

    @Composable
    fun MathBlock(node: MathBlock, style: MathStyle, modifier: Modifier) {
        BasicText(
            text = node.literal,
            modifier = modifier
                .clip(RoundedCornerShape(style.cornerRadius))
                .background(style.background)
                .padding(style.padding),
            style = TextStyle(
                fontSize = style.fontSize,
                color = style.color,
                fontFamily = FontFamily.Monospace,
            ),
        )
    }

    @Composable
    fun Diagram(node: DiagramBlock, style: CodeBlockStyle, modifier: Modifier) {
        BasicText(
            text = "[${node.diagramType}]\n${node.literal}",
            modifier = modifier
                .clip(RoundedCornerShape(style.cornerRadius))
                .background(style.background)
                .padding(style.padding),
            style = style.textStyle,
        )
    }

    @Composable
    fun BlockImage(node: Image, altText: String, style: ImageStyle, modifier: Modifier) {
        BasicText(
            text = altText.ifEmpty { node.destination },
            modifier = modifier,
            style = style.captionTextStyle,
        )
    }

    @Composable
    fun Figure(node: Figure, style: FigureStyle, modifier: Modifier) {
        Column(modifier = modifier) {
            BasicText(
                text = "[image] ${node.imageUrl}",
                style = TextStyle(fontSize = 13.sp),
            )
            if (node.caption.isNotEmpty()) {
                BasicText(
                    text = node.caption,
                    modifier = Modifier.padding(top = style.captionTopPadding),
                    style = style.captionTextStyle,
                )
            }
        }
    }

    @Composable
    fun CustomContainer(
        node: CustomContainer,
        theme: MarkdownTheme,
        modifier: Modifier,
        renderContent: @Composable () -> Unit,
    ) {
        Column(
            modifier = modifier.padding(theme.admonition.padding),
            verticalArrangement = Arrangement.spacedBy(theme.document.blockSpacing),
        ) {
            if (node.title.isNotEmpty()) {
                BasicText(text = node.title, style = theme.admonition.titleTextStyle)
            }
            renderContent()
        }
    }

    @Composable
    fun ShortcodeBlock(
        node: ShortcodeBlock,
        theme: MarkdownTheme,
        modifier: Modifier,
        renderContent: @Composable () -> Unit,
    ) {
        Column(modifier = modifier) {
            BasicText(
                text = "{% ${node.tagName} %}",
                style = spanStyleToTextStyle(theme.inlineCode.textStyle),
            )
            renderContent()
        }
    }

    @Composable
    fun TabBlock(node: TabBlock, theme: MarkdownTheme, modifier: Modifier) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(theme.document.blockSpacing),
        ) {
            for (child in node.children) {
                val title = (child as? TabItem)?.title.orEmpty()
                BasicText(
                    text = "▶ $title",
                    style = theme.paragraph.textStyle.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

private fun spanStyleToTextStyle(span: SpanStyle): TextStyle = TextStyle(
    color = span.color,
    fontSize = span.fontSize,
    fontWeight = span.fontWeight,
    fontStyle = span.fontStyle,
    fontFamily = span.fontFamily,
    background = span.background,
    textDecoration = span.textDecoration,
)
