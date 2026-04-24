package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.rememberTextMeasurer
import com.hrm.markdown.parser.ast.Image
import com.hrm.markdown.parser.ast.Node
import com.hrm.markdown.parser.ast.Paragraph
import com.hrm.markdown.parser.ast.Text
import com.hrm.markdown.ui.LocalImageRenderer
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.LocalOnLinkClick
import com.hrm.markdown.ui.MarkdownImageData
import com.hrm.markdown.ui.MarkdownImageFrame
import com.hrm.markdown.ui.extension.InlineExtensionContext
import com.hrm.markdown.ui.inline.buildInlineAnnotatedString
import com.hrm.markdown.ui.inline.buildInlineExtensionSlots
import com.hrm.markdown.ui.inline.rememberInlineContent
import com.hrm.markdown.ui.theme.LocalMarkdownTheme

/**
 * 段落渲染器。
 *
 * 当段落中不包含图片时，直接使用 BasicText + InlineTextContent 渲染富文本。
 * 当段落中包含图片时，将段落拆分为「文本段」和「图片段」，
 * 图片作为独立的块级 Composable 渲染，避免 InlineTextContent 的 Placeholder 尺寸限制
 * 导致图片无法正常显示。
 */
@Composable
internal fun ParagraphRenderer(
    node: Paragraph,
    modifier: Modifier = Modifier,
) {
    val hasImage = remember(node) { node.children.any { it is Image } }

    if (!hasImage) {
        // 无图片的段落：保持原有的简单渲染路径
        SimpleParagraphRenderer(node, modifier)
    } else {
        // 包含图片的段落：拆分为文本段和图片段
        MixedParagraphRenderer(node, modifier)
    }
}

/**
 * 简单段落渲染（不含图片）。
 */
@Composable
private fun SimpleParagraphRenderer(
    node: Paragraph,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val onLinkClick = LocalOnLinkClick.current

    WithInlineContentWidth(modifier = modifier) { maxInlineContentWidth ->
        val (annotated, inlineContents) = rememberInlineContent(
            node,
            onLinkClick,
            maxInlineContentWidth,
        )

        BasicText(
            text = annotated,
            style = theme.bodyStyle,
            inlineContent = inlineContents,
        )
    }
}

/**
 * 段落内容片段：文本或图片。
 */
private sealed class ParagraphSegment {
    /** 一段连续的非图片行内节点 */
    data class TextRun(val nodes: List<Node>) : ParagraphSegment()

    /** 图片节点 */
    data class ImageItem(val image: Image) : ParagraphSegment()
}

/**
 * 混合段落渲染（包含图片）。
 * 将段落的子节点拆分为文本段和图片段，分别渲染。
 */
@Composable
private fun MixedParagraphRenderer(
    node: Paragraph,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val onLinkClick = LocalOnLinkClick.current
    val customRenderer = LocalImageRenderer.current
    val extensionProvider = LocalMarkdownExtensionProvider.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val textMeasurer = rememberTextMeasurer()

    // 将段落子节点拆分为文本段和图片段
    val segments = remember(node) { splitParagraphSegments(node.children) }

    WithInlineContentWidth(modifier = modifier) { maxInlineContentWidth ->
        val extensionContext = remember(
            density,
            layoutDirection,
            textMeasurer,
            maxInlineContentWidth,
        ) {
            InlineExtensionContext(
                density = density,
                layoutDirection = layoutDirection,
                textMeasurer = textMeasurer,
                maxInlineContentWidth = maxInlineContentWidth,
            )
        }
        Column {
            for (segment in segments) {
                when (segment) {
                    is ParagraphSegment.TextRun -> {
                        val inlineContents = mutableMapOf<String, InlineTextContent>()
                        val extensionSlots = remember(
                            segment.nodes,
                            theme,
                            extensionProvider,
                            extensionContext,
                        ) {
                            buildInlineExtensionSlots(
                                segment.nodes,
                                theme,
                                extensionProvider,
                                extensionContext,
                            )
                        }
                        val annotated = buildInlineAnnotatedString(
                            segment.nodes,
                            theme,
                            extensionSlots,
                            inlineContents,
                            onLinkClick,
                            maxInlineContentWidth,
                            density,
                            layoutDirection,
                            textMeasurer,
                        )
                        if (annotated.isNotEmpty()) {
                            BasicText(
                                text = annotated,
                                modifier = Modifier,
                                style = theme.bodyStyle,
                                inlineContent = inlineContents,
                            )
                        }
                    }

                    is ParagraphSegment.ImageItem -> {
                        val img = segment.image
                        val altText = img.children.filterIsInstance<Text>()
                            .joinToString("") { it.literal }
                        val imageData = MarkdownImageData(
                            url = img.destination,
                            altText = altText,
                            title = img.title,
                            width = img.imageWidth,
                            height = img.imageHeight,
                            attributes = img.attributes,
                        )
                        if (customRenderer != null) {
                            MarkdownImageFrame(
                                data = imageData,
                                style = theme.image,
                                modifier = theme.modifiers.image,
                            ) {
                                customRenderer(imageData, Modifier)
                            }
                        } else {
                            extensionProvider.BlockImage(
                                node = img,
                                altText = altText,
                                style = theme.image,
                                modifier = theme.modifiers.image,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 将段落的子节点列表按图片边界拆分为多个 Segment。
 * 连续的非图片节点合并为一个 TextRun，图片节点独立为 ImageItem。
 */
private fun splitParagraphSegments(children: List<Node>): List<ParagraphSegment> {
    val segments = mutableListOf<ParagraphSegment>()
    val currentTextNodes = mutableListOf<Node>()

    for (child in children) {
        if (child is Image) {
            // 遇到图片前，先把积累的文本节点收集为一个 TextRun
            if (currentTextNodes.isNotEmpty()) {
                segments.add(ParagraphSegment.TextRun(currentTextNodes.toList()))
                currentTextNodes.clear()
            }
            segments.add(ParagraphSegment.ImageItem(child))
        } else {
            currentTextNodes.add(child)
        }
    }

    // 收集尾部的文本节点
    if (currentTextNodes.isNotEmpty()) {
        segments.add(ParagraphSegment.TextRun(currentTextNodes.toList()))
    }

    return segments
}
