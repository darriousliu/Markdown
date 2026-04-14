package com.hrm.markdown.ui.inline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hrm.markdown.parser.ast.Abbreviation
import com.hrm.markdown.parser.ast.Autolink
import com.hrm.markdown.parser.ast.CitationReference
import com.hrm.markdown.parser.ast.ContainerNode
import com.hrm.markdown.parser.ast.Emoji
import com.hrm.markdown.parser.ast.Emphasis
import com.hrm.markdown.parser.ast.EscapedChar
import com.hrm.markdown.parser.ast.FootnoteReference
import com.hrm.markdown.parser.ast.HardLineBreak
import com.hrm.markdown.parser.ast.Highlight
import com.hrm.markdown.parser.ast.HtmlEntity
import com.hrm.markdown.parser.ast.Image
import com.hrm.markdown.parser.ast.InlineCode
import com.hrm.markdown.parser.ast.InlineHtml
import com.hrm.markdown.parser.ast.InlineMath
import com.hrm.markdown.parser.ast.InsertedText
import com.hrm.markdown.parser.ast.KeyboardInput
import com.hrm.markdown.parser.ast.Link
import com.hrm.markdown.parser.ast.Node
import com.hrm.markdown.parser.ast.RubyText
import com.hrm.markdown.parser.ast.ShortcodeInline
import com.hrm.markdown.parser.ast.SoftLineBreak
import com.hrm.markdown.parser.ast.Spoiler
import com.hrm.markdown.parser.ast.Strikethrough
import com.hrm.markdown.parser.ast.StrongEmphasis
import com.hrm.markdown.parser.ast.StyledText
import com.hrm.markdown.parser.ast.Subscript
import com.hrm.markdown.parser.ast.Superscript
import com.hrm.markdown.parser.ast.Text
import com.hrm.markdown.parser.ast.WikiLink
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
 */
@Composable
internal fun rememberInlineContent(
    parent: ContainerNode,
    onLinkClick: ((String) -> Unit)? = null,
): Pair<AnnotatedString, Map<String, InlineTextContent>> {
    val theme = LocalMarkdownTheme.current
    val extensionProvider = LocalMarkdownExtensionProvider.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val textMeasurer = rememberTextMeasurer()
    val extensionSlots = rememberInlineExtensionSlots(parent.children, theme, extensionProvider)

    return remember(parent, theme, extensionProvider, onLinkClick, density, layoutDirection, textMeasurer) {
        val inlineContents = mutableMapOf<String, InlineTextContent>()
        val annotated = buildAnnotatedString {
            renderInlineChildren(
                parent.children,
                theme,
                extensionSlots,
                inlineContents,
                onLinkClick,
                density,
                layoutDirection,
                textMeasurer,
            )
        }
        annotated to inlineContents
    }
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
    density: Density? = null,
    layoutDirection: LayoutDirection? = null,
    textMeasurer: androidx.compose.ui.text.TextMeasurer? = null,
): AnnotatedString = buildAnnotatedString {
    renderInlineChildren(
        nodes,
        theme,
        extensionSlots,
        inlineContents,
        onLinkClick,
        density,
        layoutDirection,
        textMeasurer,
    )
}

private fun AnnotatedString.Builder.renderInlineChildren(
    nodes: List<Node>,
    theme: MarkdownTheme,
    extensionSlots: Map<Node, InlineExtensionSlot>,
    inlineContents: MutableMap<String, InlineTextContent>,
    onLinkClick: ((String) -> Unit)?,
    density: Density? = null,
    layoutDirection: LayoutDirection? = null,
    textMeasurer: androidx.compose.ui.text.TextMeasurer? = null,
) {
    for (node in nodes) {
        renderInlineNode(node, theme, extensionSlots, inlineContents, onLinkClick, density, layoutDirection, textMeasurer)
    }
}

private fun AnnotatedString.Builder.renderInlineNode(
    node: Node,
    theme: MarkdownTheme,
    extensionSlots: Map<Node, InlineExtensionSlot>,
    inlineContents: MutableMap<String, InlineTextContent>,
    onLinkClick: ((String) -> Unit)?,
    density: Density? = null,
    layoutDirection: LayoutDirection? = null,
    textMeasurer: androidx.compose.ui.text.TextMeasurer? = null,
) {
    when (node) {
        is Text -> append(node.literal)

        is SoftLineBreak -> append(" ")

        is HardLineBreak -> append("\n")

        is Emphasis -> {
            withStyle(theme.emphasisStyle) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, density, layoutDirection, textMeasurer)
            }
        }

        is StrongEmphasis -> {
            withStyle(theme.strongEmphasisStyle) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, density, layoutDirection, textMeasurer)
            }
        }

        is Strikethrough -> {
            withStyle(theme.strikethroughStyle) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, density, layoutDirection, textMeasurer)
            }
        }

        is InlineCode -> {
            appendStyledInlineChip(
                idPrefix = "inlinecode",
                text = node.literal,
                textStyle = theme.inlineCodeStyle,
                background = theme.inlineCodeBackground,
                cornerRadius = theme.inlineCodeCornerRadius,
                padding = theme.inlineCodePadding,
                borderColor = theme.inlineCodeBorderColor,
                borderWidth = theme.inlineCodeBorderWidth,
                inlineContents = inlineContents,
                density = density,
                layoutDirection = layoutDirection,
                textMeasurer = textMeasurer,
            )
        }

        is Link -> {
            val linkAnnotation = LinkAnnotation.Clickable(
                tag = "link",
                styles = TextLinkStyles(style = resolveLinkStyle(theme.linkStyle, theme.linkColor)),
                linkInteractionListener = {
                    onLinkClick?.invoke(node.destination)
                },
            )
            withLink(linkAnnotation) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, density, layoutDirection, textMeasurer)
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
                styles = TextLinkStyles(style = resolveLinkStyle(theme.linkStyle, theme.linkColor)),
                linkInteractionListener = {
                    onLinkClick?.invoke(node.destination)
                },
            )
            withLink(linkAnnotation) {
                append(node.destination)
            }
        }

        is InlineHtml -> {
            withStyle(theme.inlineHtmlStyle) {
                append(node.literal)
            }
        }

        is HtmlEntity -> append(node.resolved.ifEmpty { node.literal })

        is EscapedChar -> append(node.literal)

        is FootnoteReference -> {
            val linkAnnotation = LinkAnnotation.Clickable(
                tag = "footnote",
                styles = TextLinkStyles(style = resolveFootnoteLinkStyle(theme)),
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
            withStyle(theme.highlight.textStyle) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, density, layoutDirection, textMeasurer)
            }
        }

        is Superscript -> {
            withStyle(
                theme.superscriptStyle.merge(
                    SpanStyle(baselineShift = BaselineShift.Superscript)
                )
            ) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, density, layoutDirection, textMeasurer)
            }
        }

        is Subscript -> {
            withStyle(
                theme.subscriptStyle.merge(
                    SpanStyle(baselineShift = BaselineShift.Subscript)
                )
            ) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, density, layoutDirection, textMeasurer)
            }
        }

        is InsertedText -> {
            withStyle(theme.insertedTextStyle) {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, density, layoutDirection, textMeasurer)
            }
        }

        is Emoji -> append(node.unicode ?: node.literal.ifEmpty { ":${node.shortcode}:" })

        is StyledText -> {
            val spanStyle = node.style?.let { parseCssStyleToSpanStyle(it, theme) }
                ?: inferStyleFromClasses(node.cssClasses, theme)
            if (spanStyle != null) {
                withStyle(spanStyle) {
                    renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, density, layoutDirection, textMeasurer)
                }
            } else {
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, density, layoutDirection, textMeasurer)
            }
        }

        is Abbreviation -> {
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
            appendStyledInlineChip(
                idPrefix = "kbd",
                text = node.literal,
                textStyle = theme.kbdStyle,
                background = theme.kbdBackground,
                cornerRadius = theme.kbdCornerRadius,
                padding = theme.kbdPadding,
                borderColor = theme.kbdBorderColor,
                borderWidth = theme.kbdBorderWidth,
                inlineContents = inlineContents,
                density = density,
                layoutDirection = layoutDirection,
                textMeasurer = textMeasurer,
            )
        }

        is CitationReference -> {
            val linkAnnotation = LinkAnnotation.Clickable(
                tag = "citation",
                styles = TextLinkStyles(style = resolveFootnoteLinkStyle(theme)),
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
            val plainText = extractPlainText(node)
            val fontSize = theme.bodyStyle.fontSize.value
            val avgCharWidth = plainText.sumOf { ch ->
                if (ch.code > 0x7F) 12 else 7
            }.toFloat() / 10f * (fontSize / 16f)
            val placeholderWidth = (avgCharWidth + 8f).sp
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
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = theme.bodyStyle.fontSize * 0.875f,
                        color = theme.linkColor,
                    )
                ) {
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
                styles = TextLinkStyles(style = resolveLinkStyle(theme.linkStyle, theme.linkColor)),
                linkInteractionListener = {
                    onLinkClick?.invoke(node.target)
                },
            )
            withLink(linkAnnotation) {
                append(node.label ?: node.target)
            }
        }

        is RubyText -> {
            val id = "ruby_${node.hashCode()}"
            val fontSize = theme.bodyStyle.fontSize.value
            val baseWidth = node.base.sumOf { ch ->
                if (ch.code > 0x7F) 12 else 7
            }.toFloat() / 10f * (fontSize / 16f)
            val placeholderWidth = (baseWidth + 2f).sp
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
                renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, density, layoutDirection, textMeasurer)
            }
        }
    }
}

private fun AnnotatedString.Builder.appendStyledInlineChip(
    idPrefix: String,
    text: String,
    textStyle: SpanStyle,
    background: Color,
    cornerRadius: Dp,
    padding: PaddingValues,
    borderColor: Color?,
    borderWidth: Dp,
    inlineContents: MutableMap<String, InlineTextContent>,
    density: Density?,
    layoutDirection: LayoutDirection?,
    textMeasurer: androidx.compose.ui.text.TextMeasurer?,
) {
    val displayText = text.ifEmpty { " " }
    val id = "${idPrefix}_${displayText.hashCode()}_${inlineContents.size}"
    val (width, height) = measureInlineChipPlaceholder(
        text = displayText,
        textStyle = textStyle,
        padding = padding,
        density = density,
        layoutDirection = layoutDirection,
        textMeasurer = textMeasurer,
    )
    appendInlineContent(id, displayText)
    inlineContents[id] = InlineTextContent(
        placeholder = Placeholder(
            width = width,
            height = height,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
        ),
    ) {
        InlineChipContent(
            text = displayText,
            textStyle = spanStyleToTextStyle(textStyle),
            background = background,
            cornerRadius = cornerRadius,
            padding = padding,
            borderColor = borderColor,
            borderWidth = borderWidth,
        )
    }
}

private fun measureInlineChipPlaceholder(
    text: String,
    textStyle: SpanStyle,
    padding: PaddingValues,
    density: Density?,
    layoutDirection: LayoutDirection?,
    textMeasurer: androidx.compose.ui.text.TextMeasurer?,
): Pair<TextUnit, TextUnit> {
    val resolvedTextStyle = spanStyleToTextStyle(textStyle)
    val resolvedLayoutDirection = layoutDirection ?: LayoutDirection.Ltr
    val horizontalPadding = padding.calculateLeftPadding(resolvedLayoutDirection) +
        padding.calculateRightPadding(resolvedLayoutDirection)
    val verticalPadding = padding.calculateTopPadding() + padding.calculateBottomPadding()
    if (density != null && textMeasurer != null) {
        val result = textMeasurer.measure(
            text = AnnotatedString(text),
            style = resolvedTextStyle,
        )
        val widthPx = result.size.width + with(density) { horizontalPadding.roundToPx() }
        val heightPx = result.size.height + with(density) { verticalPadding.roundToPx() }
        return with(density) { widthPx.toSp() to heightPx.toSp() }
    }

    val fontSize = resolvedTextStyle.fontSize.takeIf { it != TextUnit.Unspecified }?.value ?: 14f
    val avgCharWidth = text.sumOf { if (it.code > 0x7F) 12 else 7 }.toFloat() / 10f * (fontSize / 16f)
    return (avgCharWidth + fontSize * 0.2f + horizontalPadding.value).sp to
        (fontSize * 1.35f + verticalPadding.value).sp
}

private fun resolveLinkStyle(style: SpanStyle, fallbackColor: Color): SpanStyle =
    style.merge(
        SpanStyle(
            color = if (style.color == Color.Unspecified) fallbackColor else Color.Unspecified,
        )
    )

private fun resolveFootnoteLinkStyle(theme: MarkdownTheme): SpanStyle =
    resolveLinkStyle(theme.linkStyle, theme.linkColor)
        .merge(theme.footnoteStyle)
        .merge(SpanStyle(baselineShift = BaselineShift.Superscript))

private fun spanStyleToTextStyle(span: SpanStyle): TextStyle = TextStyle(
    color = span.color,
    fontSize = span.fontSize,
    fontWeight = span.fontWeight,
    fontStyle = span.fontStyle,
    fontFamily = span.fontFamily,
    background = span.background,
    textDecoration = span.textDecoration,
)

@Composable
private fun InlineChipContent(
    text: String,
    textStyle: TextStyle,
    background: Color,
    cornerRadius: Dp,
    padding: PaddingValues,
    borderColor: Color?,
    borderWidth: Dp,
) {
    val shape = RoundedCornerShape(cornerRadius)
    var chipModifier = Modifier
        .clip(shape)
        .background(background)

    if (borderColor != null && borderWidth > 0.dp) {
        chipModifier = chipModifier.border(borderWidth, borderColor, shape)
    }

    Box(
        modifier = chipModifier.padding(padding),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicText(
            text = text,
            style = textStyle,
        )
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
            val hex = value.removePrefix("#")
            when (hex.length) {
                6 -> try {
                    Color(("FF$hex").toLong(16))
                } catch (_: Exception) {
                    null
                }
                8 -> try {
                    Color(hex.toLong(16))
                } catch (_: Exception) {
                    null
                }
                3 -> try {
                    val r = hex[0].toString().repeat(2)
                    val g = hex[1].toString().repeat(2)
                    val b = hex[2].toString().repeat(2)
                    Color(("FF$r$g$b").toLong(16))
                } catch (_: Exception) {
                    null
                }
                else -> null
            }
        }
    }
}

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

private fun extractPlainText(node: Node): String = buildString {
    when (node) {
        is Text -> append(node.literal)
        is InlineCode -> append(node.literal)
        is ContainerNode -> node.children.forEach { append(extractPlainText(it)) }
        else -> Unit
    }
}

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
                withStyle(
                    SpanStyle(
                        background = theme.spoilerColor,
                    )
                ) {
                    renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, null, null, null)
                }
            } else {
                withStyle(
                    SpanStyle(
                        background = theme.spoilerColor,
                        color = theme.spoilerColor,
                    )
                ) {
                    renderInlineChildren(node.children, theme, extensionSlots, inlineContents, onLinkClick, null, null, null)
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

@Composable
private fun RubyTextContent(
    base: String,
    annotation: String,
    theme: MarkdownTheme,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(
            text = annotation,
            style = theme.bodyStyle.copy(
                fontSize = theme.bodyStyle.fontSize * 0.5f,
                lineHeight = theme.bodyStyle.fontSize * 0.6f,
            ),
        )
        BasicText(
            text = base,
            style = theme.bodyStyle.copy(
                lineHeight = theme.bodyStyle.fontSize * 1.2f,
            ),
        )
    }
}
