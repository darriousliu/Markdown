package com.hrm.markdown.ui.inline

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.hrm.markdown.parser.ast.*
import com.hrm.markdown.parser.log.HLog
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.LocalMarkdownTheme
import com.hrm.markdown.ui.MarkdownTheme
import com.hrm.markdown.ui.extension.InlineExtensionSlot
import com.hrm.markdown.ui.extension.MarkdownExtensionProvider

/**
 * 将容器节点的子节点渲染为 AnnotatedString。
 * 这是行内渲染的核心：递归遍历行内 AST 节点，
 * 构建带样式标注的富文本。
 *
 * 对于无法内联的元素（如 LaTeX 行内公式），使用 InlineTextContent 机制。
 *
 * @return Triple<AnnotatedString, Map<String, InlineTextContent>, Boolean>
 *         分别是标注文本、内联内容映射、是否包含链接
 */
@Composable
internal fun rememberInlineContent(
    parent: ContainerNode,
    onLinkClick: ((String) -> Unit)? = null,
): Pair<AnnotatedString, Map<String, InlineTextContent>> {
    val theme = LocalMarkdownTheme.current
    val extensionProvider = LocalMarkdownExtensionProvider.current
    val hasInlineExtensions = remember(parent) {
        containsInlineExtensionCandidates(parent.children)
    }
    val extensionSlots = if (hasInlineExtensions) {
        rememberInlineExtensionSlots(parent.children, theme, extensionProvider)
    } else {
        emptyMap()
    }

    return if (!hasInlineExtensions) {
        remember(parent, theme, onLinkClick) {
            HLog.d("InlineRenderer") {"parent: ${parent.hashCode()}, theme: ${theme.hashCode()}, onLinkClick: ${onLinkClick.hashCode()}"}
            val inlineContents = mutableMapOf<String, InlineTextContent>()
            val annotated = buildAnnotatedString {
                renderInlineChildren(
                    parent.children,
                    theme,
                    emptyMap(),
                    inlineContents,
                    onLinkClick,
                )
            }
            annotated to inlineContents
        }
    } else remember(parent, theme, extensionProvider, onLinkClick) {
        HLog.d("InlineRenderer") {"parent: ${parent.hashCode()}, theme: ${theme.hashCode()}, extensionProvider: ${extensionProvider.hashCode()}, onLinkClick: ${onLinkClick.hashCode()}"}
        val inlineContents = mutableMapOf<String, InlineTextContent>()
        val annotated = buildAnnotatedString {
            renderInlineChildren(
                parent.children,
                theme,
                extensionSlots,
                inlineContents,
                onLinkClick,
            )
        }
        annotated to inlineContents
    }
}

private fun containsInlineExtensionCandidates(nodes: List<Node>): Boolean {
    for (node in nodes) {
        when (node) {
            is InlineMath, is Image, is ShortcodeInline -> return true
            is ContainerNode -> if (containsInlineExtensionCandidates(node.children)) return true
            else -> Unit
        }
    }
    return false
}

@Composable
private fun rememberInlineExtensionSlots(
    nodes: List<Node>,
    theme: MarkdownTheme,
    provider: MarkdownExtensionProvider,
): Map<Node, InlineExtensionSlot> {
    val slots = LinkedHashMap<Node, InlineExtensionSlot>()
    collectInlineExtensionSlots(nodes, theme, provider, slots)
    return slots
}

@Composable
private fun collectInlineExtensionSlots(
    nodes: List<Node>,
    theme: MarkdownTheme,
    provider: MarkdownExtensionProvider,
    slots: MutableMap<Node, InlineExtensionSlot>,
) {
    for (node in nodes) {
        when (node) {
            is InlineMath -> provider.rememberInlineMathSlot(node, theme.math)?.let { slots[node] = it }
            is Image -> {
                val altText = node.children.filterIsInstance<Text>().joinToString("") { it.literal }
                provider.rememberInlineImageSlot(node, altText, theme.image)?.let { slots[node] = it }
            }
            is ShortcodeInline -> provider.rememberInlineShortcodeSlot(node, theme)?.let { slots[node] = it }
            is ContainerNode -> collectInlineExtensionSlots(node.children, theme, provider, slots)
            else -> Unit
        }
    }
}

/**
 * 构建行内内容的 AnnotatedString（无 remember，用于嵌套调用）。
 */
internal fun buildInlineAnnotatedString(
    nodes: List<Node>,
    theme: MarkdownTheme,
    extensionSlots: Map<Node, InlineExtensionSlot>,
    inlineContents: MutableMap<String, InlineTextContent>,
    onLinkClick: ((String) -> Unit)? = null,
): AnnotatedString = buildAnnotatedString {
    renderInlineChildren(
        nodes,
        theme,
        extensionSlots,
        inlineContents,
        onLinkClick,
    )
}

private fun AnnotatedString.Builder.renderInlineChildren(
    nodes: List<Node>,
    theme: MarkdownTheme,
    extensionSlots: Map<Node, InlineExtensionSlot>,
    inlineContents: MutableMap<String, InlineTextContent>,
    onLinkClick: ((String) -> Unit)?,
) {
    for (node in nodes) {
        renderInlineNode(node, theme, extensionSlots, inlineContents, onLinkClick)
    }
}

private fun AnnotatedString.Builder.renderInlineNode(
    node: Node,
    theme: MarkdownTheme,
    extensionSlots: Map<Node, InlineExtensionSlot>,
    inlineContents: MutableMap<String, InlineTextContent>,
    onLinkClick: ((String) -> Unit)?,
) {
    when (node) {
        is Text -> append(node.literal)

        is SoftLineBreak -> append(" ")

        is HardLineBreak -> append("\n")

        is Emphasis -> {
            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
            }
        }

        is StrongEmphasis -> {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
            }
        }

        is Strikethrough -> {
            withStyle(theme.strikethroughStyle) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
            }
        }

        is InlineCode -> {
            withStyle(theme.inlineCodeStyle) {
                append(node.literal)
            }
        }

        is Link -> {
            val linkAnnotation = LinkAnnotation.Clickable(
                tag = "link",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = theme.linkColor,
                        textDecoration = TextDecoration.Underline,
                    ),
                ),
                linkInteractionListener = {
                    onLinkClick?.invoke(node.destination)
                },
            )
            withLink(linkAnnotation) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
            }
        }

        is Image -> {
            val slot = extensionSlots[node]
            if (slot != null) {
                val id = "img_${node.hashCode()}"
                appendInlineContent(id, node.title ?: node.destination)
                inlineContents[id] = InlineTextContent(
                    placeholder = Placeholder(
                        width = slot.width,
                        height = slot.height,
                        placeholderVerticalAlign = slot.verticalAlign,
                    ),
                ) {
                    slot.content()
                }
            } else {
                append(node.children.filterIsInstance<Text>().joinToString("") { it.literal }.ifEmpty { node.destination })
            }
        }

        is Autolink -> {
            val linkAnnotation = LinkAnnotation.Clickable(
                tag = "link",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = theme.linkColor,
                        textDecoration = TextDecoration.Underline,
                    ),
                ),
                linkInteractionListener = {
                    onLinkClick?.invoke(node.destination)
                },
            )
            withLink(linkAnnotation) {
                append(node.destination)
            }
        }

        is InlineHtml -> {
            withStyle(SpanStyle(color = Color.Gray, fontFamily = FontFamily.Monospace, fontSize = 14.sp)) {
                append(node.literal)
            }
        }

        is HtmlEntity -> {
            append(node.resolved.ifEmpty { node.literal })
        }

        is EscapedChar -> {
            append(node.literal)
        }

        is FootnoteReference -> {
            val linkAnnotation = LinkAnnotation.Clickable(
                tag = "footnote",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = theme.linkColor,
                        fontSize = theme.footnoteStyle.fontSize,
                        baselineShift = BaselineShift.Superscript,
                    ),
                ),
                linkInteractionListener = {
                    // 脚注点击暂不处理，可扩展
                },
            )
            withLink(linkAnnotation) {
                append("[${node.index}]")
            }
        }

        is InlineMath -> {
            val slot = extensionSlots[node]
            if (slot != null) {
                val id = "math_${node.hashCode()}"
                appendInlineContent(id, node.literal)
                inlineContents[id] = InlineTextContent(
                    placeholder = Placeholder(
                        width = slot.width,
                        height = slot.height,
                        placeholderVerticalAlign = slot.verticalAlign,
                    ),
                ) {
                    slot.content()
                }
            } else {
                append(node.literal)
            }
        }

        is Highlight -> {
            withStyle(SpanStyle(background = theme.highlightColor)) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
            }
        }

        is Superscript -> {
            withStyle(
                theme.superscriptStyle.merge(
                    SpanStyle(baselineShift = BaselineShift.Superscript)
                )
            ) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
            }
        }

        is Subscript -> {
            withStyle(
                theme.subscriptStyle.merge(
                    SpanStyle(baselineShift = BaselineShift.Subscript)
                )
            ) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
            }
        }

        is InsertedText -> {
            withStyle(theme.insertedTextStyle) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
            }
        }

        is Emoji -> {
            // 优先显示 unicode（已映射），否则显示 literal
            append(node.unicode ?: node.literal.ifEmpty { ":${node.shortcode}:" })
        }

        is StyledText -> {
            // 从属性中提取样式信息
            val styleStr = node.style
            val spanStyle = if (styleStr != null) {
                parseCssStyleToSpanStyle(styleStr, theme)
            } else {
                // 根据 CSS class 推断样式
                inferStyleFromClasses(node.cssClasses, theme)
            }
            if (spanStyle != null) {
                withStyle(spanStyle) {
                    renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
                }
            } else {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
            }
        }

        is Abbreviation -> {
            // embed fullText as annotation so consumers can show tooltip on hover/click
            if (node.fullText.isNotEmpty()) {
                pushStringAnnotation(tag = "abbreviation", annotation = node.fullText)
                withStyle(theme.abbreviationStyle) {
                    append(node.abbreviation)
                }
                pop()
            } else {
                withStyle(theme.abbreviationStyle) {
                    append(node.abbreviation)
                }
            }
        }

        is KeyboardInput -> {
            // 渲染键盘按键：等宽字体 + 背景
            withStyle(theme.kbdStyle) {
                append(node.literal)
            }
        }

        is CitationReference -> {
            val linkAnnotation = LinkAnnotation.Clickable(
                tag = "citation",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = theme.linkColor,
                        fontSize = theme.footnoteStyle.fontSize,
                        baselineShift = BaselineShift.Superscript,
                    ),
                ),
                linkInteractionListener = {
                    // 引用点击暂不处理，可扩展
                },
            )
            withLink(linkAnnotation) {
                append("[${node.key}]")
            }
        }

        is Spoiler -> {
            val id = "spoiler_${node.hashCode()}"
            // 提取纯文本用于估算占位符尺寸
            val plainText = extractPlainText(node)
            val fontSize = theme.bodyStyle.fontSize.value
            // 估算宽度：字符数 * 字体大小 * 0.6（中文字符更宽，取较大系数）
            val avgCharWidth = plainText.sumOf { ch ->
                if (ch.code > 0x7F) 12 else 7 // 中文字符约等宽，英文约半宽
            }.toFloat() / 10f * (fontSize / 16f)
            val placeholderWidth = (avgCharWidth + 8f).sp // 加上一点 padding
            val placeholderHeight = (fontSize * 1.5f).sp

            appendInlineContent(id, plainText)
            inlineContents[id] = InlineTextContent(
                placeholder = Placeholder(
                    width = placeholderWidth,
                    height = placeholderHeight,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                ),
            ) {
                SpoilerContent(
                    node = node,
                    theme = theme,
                    extensionSlots = extensionSlots,
                    inlineContents = inlineContents,
                    onLinkClick = onLinkClick,
                )
            }
        }

        is ShortcodeInline -> {
            val slot = extensionSlots[node]
            if (slot != null) {
                val id = "shortcode_${node.hashCode()}"
                appendInlineContent(id, node.tagName)
                inlineContents[id] = InlineTextContent(
                    placeholder = Placeholder(
                        width = slot.width,
                        height = slot.height,
                        placeholderVerticalAlign = slot.verticalAlign,
                    ),
                ) {
                    slot.content()
                }
            } else {
                withStyle(SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = theme.bodyStyle.fontSize * 0.875f,
                    color = theme.linkColor,
                )) {
                    val argsText = if (node.args.isNotEmpty()) {
                        " " + node.args.entries.joinToString(" ") { (k, v) ->
                            if (k.startsWith("_")) v else "$k=$v"
                        }
                    } else ""
                    append("{% ${node.tagName}$argsText %}")
                }
            }
        }

        is WikiLink -> {
            val linkAnnotation = LinkAnnotation.Clickable(
                tag = "wikilink",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = theme.linkColor,
                        textDecoration = TextDecoration.Underline,
                    ),
                ),
                linkInteractionListener = {
                    onLinkClick?.invoke(node.target)
                },
            )
            withLink(linkAnnotation) {
                append(node.label ?: node.target)
            }
        }

        is RubyText -> {
            // 渲染 Ruby 注音：使用 InlineTextContent 机制
            val id = "ruby_${node.hashCode()}"
            val fontSize = theme.bodyStyle.fontSize.value
            // 估算宽度：基础文本字符数 * 字体大小
            val baseWidth = node.base.sumOf { ch ->
                if (ch.code > 0x7F) 12 else 7
            }.toFloat() / 10f * (fontSize / 16f)
            val placeholderWidth = (baseWidth + 2f).sp
            // 高度需要额外空间放置注音
            val placeholderHeight = (fontSize * 2.0f).sp

            appendInlineContent(id, node.base)
            inlineContents[id] = InlineTextContent(
                placeholder = Placeholder(
                    width = placeholderWidth,
                    height = placeholderHeight,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                ),
            ) {
                RubyTextContent(
                    base = node.base,
                    annotation = node.annotation,
                    theme = theme,
                )
            }
        }

        else -> {
            if (node is ContainerNode) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
            }
        }
    }
}

/**
 * 简易 CSS style 字符串转 SpanStyle。
 * 支持常见属性：color, background, font-weight, font-style, text-decoration, font-size。
 */
private fun parseCssStyleToSpanStyle(css: String, theme: MarkdownTheme): SpanStyle? {
    if (css.isBlank()) return null
    var color: Color? = null
    var background: Color? = null
    var fontWeight: FontWeight? = null
    var fontStyle: FontStyle? = null
    var textDecoration: TextDecoration? = null

    val pairs = css.split(";").map { it.trim() }.filter { it.isNotEmpty() }
    for (pair in pairs) {
        val colonIdx = pair.indexOf(':')
        if (colonIdx < 0) continue
        val key = pair.substring(0, colonIdx).trim().lowercase()
        val value = pair.substring(colonIdx + 1).trim().lowercase()
        when (key) {
            "color" -> color = parseCssColor(value)
            "background", "background-color" -> background = parseCssColor(value)
            "font-weight" -> fontWeight = when (value) {
                "bold" -> FontWeight.Bold
                "normal" -> FontWeight.Normal
                "lighter" -> FontWeight.Light
                else -> null
            }
            "font-style" -> fontStyle = when (value) {
                "italic" -> FontStyle.Italic
                "normal" -> FontStyle.Normal
                else -> null
            }
            "text-decoration" -> textDecoration = when {
                "underline" in value -> TextDecoration.Underline
                "line-through" in value -> TextDecoration.LineThrough
                else -> null
            }
        }
    }
    return SpanStyle(
        color = color ?: Color.Unspecified,
        background = background ?: Color.Unspecified,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        textDecoration = textDecoration,
    )
}

/**
 * 简易 CSS 颜色解析（支持命名色和 hex）。
 */
private fun parseCssColor(value: String): Color? {
    return when (value.trim().lowercase()) {
        "red" -> Color.Red
        "blue" -> Color.Blue
        "green" -> Color.Green
        "yellow" -> Color.Yellow
        "white" -> Color.White
        "black" -> Color.Black
        "gray", "grey" -> Color.Gray
        "cyan" -> Color.Cyan
        "magenta" -> Color.Magenta
        "orange" -> Color(0xFFFFA500)
        "purple" -> Color(0xFF800080)
        "pink" -> Color(0xFFFF69B4)
        else -> {
            // 尝试 hex 颜色
            val hex = value.removePrefix("#")
            when (hex.length) {
                6 -> try { Color(("FF$hex").toLong(16)) } catch (_: Exception) { null }
                8 -> try { Color(hex.toLong(16)) } catch (_: Exception) { null }
                3 -> try {
                    val r = hex[0].toString().repeat(2)
                    val g = hex[1].toString().repeat(2)
                    val b = hex[2].toString().repeat(2)
                    Color(("FF$r$g$b").toLong(16))
                } catch (_: Exception) { null }
                else -> null
            }
        }
    }
}

/**
 * 根据 CSS class 名推断 SpanStyle。
 * 支持常见约定类名：red, blue, green, bold, italic, underline, highlight 等。
 */
private fun inferStyleFromClasses(classes: List<String>, theme: MarkdownTheme): SpanStyle? {
    if (classes.isEmpty()) return null
    var color: Color? = null
    var background: Color? = null
    var fontWeight: FontWeight? = null
    var fontStyle: FontStyle? = null
    var textDecoration: TextDecoration? = null

    for (cls in classes) {
        when (cls.lowercase()) {
            "red" -> color = Color.Red
            "blue" -> color = Color.Blue
            "green" -> color = Color.Green
            "yellow" -> color = Color.Yellow
            "orange" -> color = Color(0xFFFFA500)
            "purple" -> color = Color(0xFF800080)
            "pink" -> color = Color(0xFFFF69B4)
            "gray", "grey" -> color = Color.Gray
            "bold" -> fontWeight = FontWeight.Bold
            "italic" -> fontStyle = FontStyle.Italic
            "underline" -> textDecoration = TextDecoration.Underline
            "line-through", "strikethrough" -> textDecoration = TextDecoration.LineThrough
            "highlight" -> background = theme.highlightColor
        }
    }
    return SpanStyle(
        color = color ?: Color.Unspecified,
        background = background ?: Color.Unspecified,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        textDecoration = textDecoration,
    )
}

/**
 * 递归提取节点的纯文本内容。
 */
private fun extractPlainText(node: Node): String = buildString {
    when (node) {
        is Text -> append(node.literal)
        is InlineCode -> append(node.literal)
        is ContainerNode -> node.children.forEach { append(extractPlainText(it)) }
        else -> {}
    }
}

/**
 * 可点击的剧透文本 Composable。
 * 初始状态下文字被遮挡（文字颜色 = 背景颜色），点击后揭示文字内容。
 */
@Composable
private fun SpoilerContent(
    node: Spoiler,
    theme: MarkdownTheme,
    extensionSlots: Map<Node, InlineExtensionSlot>,
    inlineContents: MutableMap<String, InlineTextContent>,
    onLinkClick: ((String) -> Unit)?,
) {
    var revealed by remember { mutableStateOf(false) }
    val annotated = remember(node, theme, revealed) {
        buildAnnotatedString {
            if (revealed) {
                withStyle(SpanStyle(
                    background = theme.spoilerColor,
                )) {
                    renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
                }
            } else {
                withStyle(SpanStyle(
                    background = theme.spoilerColor,
                    color = theme.spoilerColor,
                )) {
                    renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick)
                }
            }
        }
    }
    BasicText(
        text = annotated,
        modifier = Modifier.clickable { revealed = !revealed },
        style = theme.bodyStyle,
    )
}

/**
 * Ruby 注音内容 Composable。
 * 将基础文本和注音文本垂直排列：注音在上，基础文本在下。
 */
@Composable
private fun RubyTextContent(
    base: String,
    annotation: String,
    theme: MarkdownTheme,
) {
    androidx.compose.foundation.layout.Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 注音文本（小字号，在上方）
        BasicText(
            text = annotation,
            style = theme.bodyStyle.copy(
                fontSize = theme.bodyStyle.fontSize * 0.5f,
                lineHeight = theme.bodyStyle.fontSize * 0.6f,
            ),
        )
        // 基础文本
        BasicText(
            text = base,
            style = theme.bodyStyle.copy(
                lineHeight = theme.bodyStyle.fontSize * 1.2f,
            ),
        )
    }
}
