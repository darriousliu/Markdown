package com.hrm.markdown.ui.inline

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
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
import com.hrm.markdown.parser.ast.StrongEmphasis
import com.hrm.markdown.parser.ast.Strikethrough
import com.hrm.markdown.parser.ast.StyledText
import com.hrm.markdown.parser.ast.Subscript
import com.hrm.markdown.parser.ast.Superscript
import com.hrm.markdown.parser.ast.Text
import com.hrm.markdown.parser.ast.WikiLink
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.LocalMarkdownLinkHandler
import com.hrm.markdown.ui.LocalMarkdownTheme
import com.hrm.markdown.ui.extension.InlineExtensionSlot
import com.hrm.markdown.ui.extension.MarkdownExtensionProvider
import com.hrm.markdown.ui.theme.MarkdownTheme

/**
 * 行内内容渲染结果。
 *
 * - [annotated]：带样式标注的文字。
 * - [inlineContents]：供 [androidx.compose.foundation.text.BasicText] 的
 *   `inlineContent` 参数使用的占位内容。
 */
data class InlineRenderResult(
    val annotated: AnnotatedString,
    val inlineContents: Map<String, InlineTextContent>,
)

/**
 * 将某个容器节点的子节点渲染为 [InlineRenderResult]。
 *
 * 行内节点直接构建 [AnnotatedString]；对 InlineMath / Image / ShortcodeInline 等
 * 需要 InlineTextContent 的节点，查询 [MarkdownExtensionProvider] 提供的 slot：
 * - 有 slot ⇒ 使用 `InlineTextContent` 占位符 + 外部实现的 content。
 * - 无 slot ⇒ 以文本形式降级（例如原始 LaTeX、alt text、tag 名称）。
 */
@Composable
fun rememberInlineContent(parent: ContainerNode): InlineRenderResult =
    rememberInlineContent(key = parent, nodes = parent.children)

/**
 * 渲染一组行内子节点，用于 Paragraph 内部按图片分段等场景。
 *
 * [key] 用作 `remember` / slot 收集的身份，请传入足以稳定识别这段节点序列的对象
 * （例如持有这些节点的 Segment / ContainerNode）。
 */
@Composable
fun rememberInlineContent(key: Any, nodes: List<Node>): InlineRenderResult {
    val theme = LocalMarkdownTheme.current
    val onLinkClick = LocalMarkdownLinkHandler.current
    val extensions = LocalMarkdownExtensionProvider.current

    val slots = collectInlineSlots(nodes, theme, extensions)

    val inlineContents = slots.mapValues { (_, slot) ->
        InlineTextContent(
            placeholder = Placeholder(
                width = slot.width,
                height = slot.height,
                placeholderVerticalAlign = slot.verticalAlign,
            ),
            children = { slot.content() },
        )
    }

    val slotKeys = slots.keys
    val annotated = remember(key, theme, onLinkClick, slotKeys) {
        buildAnnotatedString {
            renderInlineChildren(nodes, theme, onLinkClick, slotKeys)
        }
    }

    return InlineRenderResult(annotated, inlineContents)
}

/**
 * 用 Composable 遍历节点树，收集 provider 提供的 [InlineExtensionSlot]。
 */
@Composable
private fun collectInlineSlots(
    nodes: List<Node>,
    theme: MarkdownTheme,
    provider: MarkdownExtensionProvider,
): Map<String, InlineExtensionSlot> {
    val result = LinkedHashMap<String, InlineExtensionSlot>()
    visitForSlots(nodes, theme, provider, result)
    return result
}

@Composable
private fun visitForSlots(
    nodes: List<Node>,
    theme: MarkdownTheme,
    provider: MarkdownExtensionProvider,
    result: MutableMap<String, InlineExtensionSlot>,
) {
    for (node in nodes) {
        when (node) {
            is InlineMath -> provider.rememberInlineMathSlot(node, theme.math)?.let {
                result[inlineMathId(node)] = it
            }
            is Image -> {
                val alt = extractPlainText(node)
                provider.rememberInlineImageSlot(node, alt, theme.image)?.let {
                    result[inlineImageId(node)] = it
                }
            }
            is ShortcodeInline -> provider.rememberInlineShortcodeSlot(node, theme)?.let {
                result[inlineShortcodeId(node)] = it
            }
            is ContainerNode -> visitForSlots(node.children, theme, provider, result)
            else -> {}
        }
    }
}

internal fun inlineMathId(node: InlineMath): String = "math_${identityKey(node)}"
internal fun inlineImageId(node: Image): String = "img_${identityKey(node)}"
internal fun inlineShortcodeId(node: ShortcodeInline): String = "sc_${identityKey(node)}"

private fun identityKey(node: Any): String = node.hashCode().toString(16)

private fun AnnotatedString.Builder.renderInlineChildren(
    nodes: List<Node>,
    theme: MarkdownTheme,
    onLinkClick: ((String) -> Unit)?,
    availableSlots: Set<String>,
) {
    for (node in nodes) renderInlineNode(node, theme, onLinkClick, availableSlots)
}

private fun AnnotatedString.Builder.renderInlineNode(
    node: Node,
    theme: MarkdownTheme,
    onLinkClick: ((String) -> Unit)?,
    availableSlots: Set<String>,
) {
    when (node) {
        is Text -> append(node.literal)
        is SoftLineBreak -> append(" ")
        is HardLineBreak -> append("\n")

        is Emphasis -> withStyle(theme.emphasis.textStyle) {
            renderInlineChildren(node.children, theme, onLinkClick, availableSlots)
        }
        is StrongEmphasis -> withStyle(theme.strongEmphasis.textStyle) {
            renderInlineChildren(node.children, theme, onLinkClick, availableSlots)
        }
        is Strikethrough -> withStyle(theme.strikethrough.textStyle) {
            renderInlineChildren(node.children, theme, onLinkClick, availableSlots)
        }
        is Highlight -> withStyle(theme.highlight.textStyle) {
            renderInlineChildren(node.children, theme, onLinkClick, availableSlots)
        }
        is Superscript -> withStyle(
            theme.superscript.textStyle.merge(SpanStyle(baselineShift = BaselineShift.Superscript)),
        ) {
            renderInlineChildren(node.children, theme, onLinkClick, availableSlots)
        }
        is Subscript -> withStyle(
            theme.subscript.textStyle.merge(SpanStyle(baselineShift = BaselineShift.Subscript)),
        ) {
            renderInlineChildren(node.children, theme, onLinkClick, availableSlots)
        }
        is InsertedText -> withStyle(theme.insertedText.textStyle) {
            renderInlineChildren(node.children, theme, onLinkClick, availableSlots)
        }

        is InlineCode -> withStyle(
            theme.inlineCode.textStyle.copy(background = theme.inlineCode.background),
        ) {
            append(node.literal)
        }

        is Link -> {
            val annotation = LinkAnnotation.Clickable(
                tag = "link",
                styles = TextLinkStyles(style = theme.link.textStyle),
                linkInteractionListener = { onLinkClick?.invoke(node.destination) },
            )
            withLink(annotation) {
                renderInlineChildren(node.children, theme, onLinkClick, availableSlots)
            }
        }

        is Autolink -> {
            val annotation = LinkAnnotation.Clickable(
                tag = "autolink",
                styles = TextLinkStyles(style = theme.link.textStyle),
                linkInteractionListener = { onLinkClick?.invoke(node.destination) },
            )
            withLink(annotation) { append(node.destination) }
        }

        is WikiLink -> {
            val annotation = LinkAnnotation.Clickable(
                tag = "wikilink",
                styles = TextLinkStyles(style = theme.link.textStyle),
                linkInteractionListener = { onLinkClick?.invoke(node.target) },
            )
            withLink(annotation) { append(node.label ?: node.target) }
        }

        is InlineHtml -> withStyle(theme.inlineHtml.textStyle) { append(node.literal) }
        is HtmlEntity -> append(node.resolved.ifEmpty { node.literal })
        is EscapedChar -> append(node.literal)

        is FootnoteReference -> {
            val annotation = LinkAnnotation.Clickable(
                tag = "footnote",
                styles = TextLinkStyles(
                    style = theme.link.textStyle.merge(
                        SpanStyle(
                            fontSize = theme.footnote.textStyle.fontSize,
                            baselineShift = BaselineShift.Superscript,
                        ),
                    ),
                ),
                linkInteractionListener = { onLinkClick?.invoke("footnote:${node.label}") },
            )
            withLink(annotation) { append("[${node.index}]") }
        }

        is CitationReference -> {
            val annotation = LinkAnnotation.Clickable(
                tag = "citation",
                styles = TextLinkStyles(
                    style = theme.link.textStyle.merge(
                        SpanStyle(
                            fontSize = theme.footnote.textStyle.fontSize,
                            baselineShift = BaselineShift.Superscript,
                        ),
                    ),
                ),
                linkInteractionListener = { onLinkClick?.invoke("citation:${node.key}") },
            )
            withLink(annotation) { append("[${node.key}]") }
        }

        is InlineMath -> {
            val id = inlineMathId(node)
            if (id in availableSlots) {
                appendInlineContent(id, node.literal)
            } else {
                withStyle(SpanStyle(fontSize = theme.math.fontSize, color = theme.math.color)) {
                    append("\$${node.literal}\$")
                }
            }
        }

        is Image -> {
            val id = inlineImageId(node)
            if (id in availableSlots) {
                val altText = extractPlainText(node)
                appendInlineContent(id, node.title ?: altText.ifEmpty { node.destination })
            } else {
                append(extractPlainText(node).ifEmpty { node.destination })
            }
        }

        is ShortcodeInline -> {
            val id = inlineShortcodeId(node)
            if (id in availableSlots) {
                appendInlineContent(id, node.tagName)
            } else {
                withStyle(theme.inlineCode.textStyle) { append("{% ${node.tagName} %}") }
            }
        }

        is Emoji -> append(node.unicode ?: node.literal.ifEmpty { ":${node.shortcode}:" })

        is StyledText -> {
            val spanStyle = styledTextSpanStyle(node, theme)
            if (spanStyle != null) {
                withStyle(spanStyle) {
                    renderInlineChildren(node.children, theme, onLinkClick, availableSlots)
                }
            } else {
                renderInlineChildren(node.children, theme, onLinkClick, availableSlots)
            }
        }

        is Abbreviation -> {
            if (node.fullText.isNotEmpty()) {
                pushStringAnnotation(tag = "abbreviation", annotation = node.fullText)
                withStyle(theme.abbreviation.textStyle) { append(node.abbreviation) }
                pop()
            } else {
                withStyle(theme.abbreviation.textStyle) { append(node.abbreviation) }
            }
        }

        is KeyboardInput -> withStyle(
            theme.kbd.textStyle.copy(background = theme.kbd.background),
        ) { append(node.literal) }

        is RubyText -> {
            append(node.base)
            if (node.annotation.isNotEmpty()) {
                withStyle(SpanStyle(fontSize = 12.sp)) { append("(${node.annotation})") }
            }
        }

        is Spoiler -> {
            withStyle(SpanStyle(color = theme.spoiler.background, background = theme.spoiler.background)) {
                renderInlineChildren(node.children, theme, onLinkClick, availableSlots)
            }
        }

        else -> {
            if (node is ContainerNode) {
                renderInlineChildren(node.children, theme, onLinkClick, availableSlots)
            }
        }
    }
}

/** 提取节点的纯文本（alt / 注音降级等场景使用）。 */
internal fun extractPlainText(node: Node): String = buildString {
    fun visit(n: Node) {
        when (n) {
            is Text -> append(n.literal)
            is InlineCode -> append(n.literal)
            is ContainerNode -> n.children.forEach(::visit)
            else -> {}
        }
    }
    visit(node)
}

private fun styledTextSpanStyle(node: StyledText, theme: MarkdownTheme): SpanStyle? {
    if (node.style.isNullOrBlank() && node.cssClasses.isEmpty()) return null
    var span = SpanStyle()
    var changed = false
    for (cls in node.cssClasses) {
        when (cls.lowercase()) {
            "bold" -> { span = span.merge(theme.strongEmphasis.textStyle); changed = true }
            "italic" -> { span = span.merge(theme.emphasis.textStyle); changed = true }
            "underline" -> { span = span.merge(theme.insertedText.textStyle); changed = true }
            "highlight" -> { span = span.merge(theme.highlight.textStyle); changed = true }
        }
    }
    return if (changed) span else null
}
