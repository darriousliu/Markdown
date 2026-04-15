package com.hrm.markdown.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import com.hrm.markdown.parser.ast.Figure
import com.hrm.markdown.ui.LocalImageRenderer
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.MarkdownImageData
import com.hrm.markdown.ui.MarkdownImageFrame
import com.hrm.markdown.ui.theme.FigureContentAlignment
import com.hrm.markdown.ui.theme.LocalMarkdownTheme

/**
 * Figure 渲染器：将 Figure 节点渲染为图片 + 标题（figcaption）。
 *
 * 居中显示图片，下方显示斜体标题文本。
 */
@Composable
internal fun FigureRenderer(
    node: Figure,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val imageRenderer = LocalImageRenderer.current
    if (imageRenderer != null) {
        val figureShape = RoundedCornerShape(theme.figureCornerRadius)
        Column(
            modifier = modifier
                .clip(figureShape)
                .background(theme.figureBackground)
                .border(theme.figureBorderWidth, theme.figureBorderColor, figureShape)
                .padding(theme.figurePadding),
            horizontalAlignment = when (theme.figureContentAlignment) {
                FigureContentAlignment.Start -> Alignment.Start
                FigureContentAlignment.Center -> Alignment.CenterHorizontally
                FigureContentAlignment.End -> Alignment.End
            },
        ) {
            val imageData = MarkdownImageData(
                url = node.imageUrl,
                altText = node.caption,
                title = node.caption,
                width = node.imageWidth,
                height = node.imageHeight,
                attributes = node.attributes,
            )
            MarkdownImageFrame(
                data = imageData,
                style = theme.image,
            ) {
                imageRenderer(imageData, Modifier)
            }
            if (node.caption.isNotEmpty()) {
                BasicText(
                    text = node.caption,
                    modifier = Modifier.padding(top = theme.figure.caption.topPadding),
                    style = theme.figure.caption.textStyle.copy(
                        fontStyle = if (theme.figure.caption.italic) FontStyle.Italic else FontStyle.Normal,
                        textAlign = theme.figure.caption.textAlign,
                    ),
                )
            }
        }
    } else {
        LocalMarkdownExtensionProvider.current.Figure(
            node = node,
            style = theme.figure,
            modifier = modifier,
        )
    }
}
