package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hrm.markdown.parser.ast.Figure
import com.hrm.markdown.ui.LocalImageRenderer
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.LocalMarkdownTheme
import com.hrm.markdown.ui.MarkdownImageData

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
        Column(modifier = modifier) {
            imageRenderer(
                MarkdownImageData(
                    url = node.imageUrl,
                    altText = node.caption,
                    title = node.caption,
                    width = node.imageWidth,
                    height = node.imageHeight,
                    attributes = node.attributes,
                ),
                Modifier,
            )
            if (node.caption.isNotEmpty()) {
                BasicText(
                    text = node.caption,
                    modifier = Modifier.padding(top = theme.figure.captionTopPadding),
                    style = theme.figure.captionTextStyle.copy(
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
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
