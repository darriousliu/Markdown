package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.Image
import com.hrm.markdown.parser.ast.Node
import com.hrm.markdown.parser.ast.Paragraph
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.LocalMarkdownModifiers
import com.hrm.markdown.ui.LocalMarkdownTheme
import com.hrm.markdown.ui.inline.extractPlainText
import com.hrm.markdown.ui.inline.rememberInlineContent

/**
 * 段落渲染器。
 *
 * 若段落内部含有图片节点，会拆分为「文本段 + 块级图片」交替渲染，
 * 避免 InlineTextContent Placeholder 的尺寸限制让图片被压成小方块。
 */
@Composable
internal fun ParagraphRenderer(node: Paragraph, modifier: Modifier = Modifier) {
    val theme = LocalMarkdownTheme.current
    val hasImage = remember(node) { node.children.any { it is Image } }

    if (!hasImage) {
        val (annotated, inlineContents) = rememberInlineContent(node)
        BasicText(
            text = annotated,
            modifier = modifier,
            style = theme.paragraph.textStyle,
            inlineContent = inlineContents,
        )
        return
    }

    MixedParagraph(node, modifier)
}

@Composable
private fun MixedParagraph(node: Paragraph, modifier: Modifier) {
    val theme = LocalMarkdownTheme.current
    val extensions = LocalMarkdownExtensionProvider.current
    val modifiers = LocalMarkdownModifiers.current

    val segments = remember(node) { splitSegments(node.children) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(theme.document.blockSpacing),
    ) {
        for (segment in segments) {
            when (segment) {
                is Segment.Text -> {
                    val (annotated, inlineContents) = rememberInlineContent(
                        key = segment,
                        nodes = segment.nodes,
                    )
                    if (annotated.isNotEmpty()) {
                        BasicText(
                            text = annotated,
                            style = theme.paragraph.textStyle,
                            inlineContent = inlineContents,
                        )
                    }
                }
                is Segment.ImageNode -> {
                    val alt = remember(segment.image) { extractPlainText(segment.image) }
                    extensions.BlockImage(
                        node = segment.image,
                        altText = alt,
                        style = theme.image,
                        modifier = modifiers.image,
                    )
                }
            }
        }
    }
}

private sealed class Segment {
    class Text(val nodes: List<Node>) : Segment()
    class ImageNode(val image: Image) : Segment()
}

private fun splitSegments(children: List<Node>): List<Segment> {
    val list = mutableListOf<Segment>()
    val buffer = mutableListOf<Node>()
    for (child in children) {
        if (child is Image) {
            if (buffer.isNotEmpty()) {
                list += Segment.Text(buffer.toList())
                buffer.clear()
            }
            list += Segment.ImageNode(child)
        } else buffer += child
    }
    if (buffer.isNotEmpty()) list += Segment.Text(buffer.toList())
    return list
}
