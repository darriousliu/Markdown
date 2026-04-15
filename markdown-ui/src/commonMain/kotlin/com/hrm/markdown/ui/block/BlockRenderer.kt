package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import com.hrm.markdown.parser.ast.*
import com.hrm.markdown.ui.LocalMarkdownTheme
import com.hrm.markdown.ui.LocalRendererDocument

private fun List<IntRange>.flattenLineNumbers(): Set<Int> = buildSet {
    for (range in this@flattenLineNumbers) {
        addAll(range)
    }
}

/**
 * 块级节点分发器。
 * 根据节点类型分发到对应的块级渲染器。
 */
@Composable
internal fun BlockRenderer(
    node: Node,
    renderRevision: String = "",
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val themedModifier = modifier.then(
        when (node) {
            is Heading, is SetextHeading -> theme.modifiers.heading
            is Paragraph -> theme.modifiers.paragraph
            is ThematicBreak -> theme.modifiers.thematicBreak
            is FencedCodeBlock, is IndentedCodeBlock -> theme.modifiers.codeBlock
            is BlockQuote -> theme.modifiers.blockQuote
            is ListBlock -> theme.modifiers.list
            is HtmlBlock -> theme.modifiers.htmlBlock
            is Table -> theme.modifiers.table
            is MathBlock -> theme.modifiers.math
            is Admonition -> theme.modifiers.admonition
            is CustomContainer -> theme.modifiers.customContainer
            is DiagramBlock -> theme.modifiers.diagram
            is ColumnsLayout -> theme.modifiers.columns
            is DefinitionList -> theme.modifiers.definitionList
            is FootnoteDefinition -> theme.modifiers.footnoteDefinition
            is PageBreak -> theme.modifiers.pageBreak
            is ShortcodeBlock -> theme.modifiers.shortcode
            is TabBlock -> theme.modifiers.tabBlock
            is Figure -> theme.modifiers.figure
            else -> Modifier
        }
    )
    when (node) {
        is Heading -> HeadingRenderer(node, themedModifier)
        is SetextHeading -> SetextHeadingRenderer(node, themedModifier)
        is Paragraph -> ParagraphRenderer(node, themedModifier)
        is ThematicBreak -> ThematicBreakRenderer(themedModifier)
        is FencedCodeBlock -> key(renderRevision) {
            FencedCodeBlockRenderer(node, themedModifier)
        }
        is IndentedCodeBlock -> key(renderRevision) {
            IndentedCodeBlockRenderer(node, themedModifier)
        }
        is BlockQuote -> BlockQuoteRenderer(node, themedModifier)
        is ListBlock -> ListBlockRenderer(node, themedModifier)
        is HtmlBlock -> HtmlBlockRenderer(node, themedModifier)
        is Table -> TableRenderer(node, themedModifier)
        is MathBlock -> MathBlockRenderer(node, themedModifier)
        is Admonition -> AdmonitionRenderer(node, themedModifier)
        is CustomContainer -> CustomContainerRenderer(node, themedModifier)
        is DiagramBlock -> DiagramBlockRenderer(node, themedModifier)
        is ColumnsLayout -> ColumnsLayoutRenderer(node, themedModifier)
        is DefinitionList -> DefinitionListRenderer(node, themedModifier)
        is FootnoteDefinition -> FootnoteDefinitionRenderer(node, themedModifier)
        is TocPlaceholder -> TocPlaceholderRenderer(node, themedModifier)
        is PageBreak -> PageBreakRenderer(themedModifier)
        is ShortcodeBlock -> ShortcodeBlockRenderer(node, themedModifier)
        is TabBlock -> TabBlockRenderer(node, themedModifier)
        is BibliographyDefinition -> BibliographyDefinitionRenderer(node, themedModifier)
        is Figure -> FigureRenderer(node, themedModifier)
        is FrontMatter -> { /* FrontMatter 通常不渲染 */ }
        is LinkReferenceDefinition -> { /* 引用定义不直接渲染 */ }
        is AbbreviationDefinition -> { /* 缩写定义不直接渲染 */ }
        is BlankLine -> { /* 空行不渲染 */ }
        else -> {
            // 未知块级节点，尝试渲染子节点
            if (node is ContainerNode) {
                for (child in node.children) {
                    BlockRenderer(child)
                }
            }
        }
    }
}

internal fun blockRenderRevision(node: Node): String = when (node) {
    is FencedCodeBlock -> "${node.lineRange.endLine}:${node.literal.length}"
    is IndentedCodeBlock -> "${node.lineRange.endLine}:${node.literal.length}"
    else -> "${node.lineRange.endLine}"
}

/**
 * TOC 占位符渲染器：渲染自动生成的目录。
 *
 * 支持高级配置：
 * - `minDepth`/`maxDepth`：过滤标题层级范围
 * - `excludeIds`：排除指定 ID 的标题
 * - `order`：排序方式（asc/desc）
 */
@Composable
internal fun TocPlaceholderRenderer(
    node: TocPlaceholder,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val document = LocalRendererDocument.current

    // 收集所有标题
    var headings = collectHeadings(document)
    if (headings.isEmpty()) return

    // 按深度范围过滤
    headings = headings.filter { it.level in node.minDepth..node.maxDepth }

    // 按排除 ID 过滤
    if (node.excludeIds.isNotEmpty()) {
        headings = headings.filter { heading ->
            heading.id == null || heading.id !in node.excludeIds
        }
    }

    // 按排序方式排序
    if (node.order == "desc") {
        headings = headings.reversed()
    }

    if (headings.isEmpty()) return

    Column(
        modifier = modifier.padding(theme.tableOfContentsPadding),
        verticalArrangement = Arrangement.spacedBy(theme.tableOfContentsItemSpacing),
    ) {
        BasicText(
            text = "Table of Contents",
            style = theme.tableOfContentsTitleTextStyle ?: theme.headingStyles.getOrElse(2) { theme.bodyStyle },
            modifier = Modifier.padding(bottom = theme.tableOfContentsTitleBottomPadding),
        )
        for ((text, level, _) in headings) {
            val adjustedLevel = (level - node.minDepth).coerceAtLeast(0)
            BasicText(
                text = "${"  ".repeat(adjustedLevel)}• $text",
                style = theme.bodyStyle.copy(
                    color = theme.linkColor,
                    fontStyle = FontStyle.Normal,
                ),
                modifier = Modifier.padding(start = theme.tableOfContentsIndentUnit * adjustedLevel),
            )
        }
    }
}

private data class HeadingInfo(val text: String, val level: Int, val id: String?)

private fun collectHeadings(document: Document): List<HeadingInfo> {
    val result = mutableListOf<HeadingInfo>()
    for (child in document.children) {
        collectHeadingsRecursive(child, result)
    }
    return result
}

private fun collectHeadingsRecursive(node: Node, result: MutableList<HeadingInfo>) {
    when (node) {
        is Heading -> {
            val text = node.children.joinToString("") { extractText(it) }
            result.add(HeadingInfo(text, node.level, node.id))
        }
        is SetextHeading -> {
            val text = node.children.joinToString("") { extractText(it) }
            result.add(HeadingInfo(text, node.level, node.id))
        }
        is ContainerNode -> {
            for (child in node.children) {
                collectHeadingsRecursive(child, result)
            }
        }
        else -> {}
    }
}

private fun extractText(node: Node): String = when (node) {
    is Text -> node.literal
    is InlineCode -> node.literal
    is EscapedChar -> node.literal
    is HtmlEntity -> node.resolved.ifEmpty { node.literal }
    is Emoji -> node.literal
    is ContainerNode -> node.children.joinToString("") { extractText(it) }
    else -> ""
}
