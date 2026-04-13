package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.AbbreviationDefinition
import com.hrm.markdown.parser.ast.Admonition
import com.hrm.markdown.parser.ast.BibliographyDefinition
import com.hrm.markdown.parser.ast.BlankLine
import com.hrm.markdown.parser.ast.BlockQuote
import com.hrm.markdown.parser.ast.ColumnsLayout
import com.hrm.markdown.parser.ast.ContainerNode
import com.hrm.markdown.parser.ast.CustomContainer
import com.hrm.markdown.parser.ast.DefinitionList
import com.hrm.markdown.parser.ast.DiagramBlock
import com.hrm.markdown.parser.ast.FencedCodeBlock
import com.hrm.markdown.parser.ast.Figure
import com.hrm.markdown.parser.ast.FootnoteDefinition
import com.hrm.markdown.parser.ast.FrontMatter
import com.hrm.markdown.parser.ast.Heading
import com.hrm.markdown.parser.ast.HtmlBlock
import com.hrm.markdown.parser.ast.IndentedCodeBlock
import com.hrm.markdown.parser.ast.LinkReferenceDefinition
import com.hrm.markdown.parser.ast.ListBlock
import com.hrm.markdown.parser.ast.MathBlock
import com.hrm.markdown.parser.ast.Node
import com.hrm.markdown.parser.ast.PageBreak
import com.hrm.markdown.parser.ast.Paragraph
import com.hrm.markdown.parser.ast.SetextHeading
import com.hrm.markdown.parser.ast.ShortcodeBlock
import com.hrm.markdown.parser.ast.TabBlock
import com.hrm.markdown.parser.ast.Table
import com.hrm.markdown.parser.ast.ThematicBreak
import com.hrm.markdown.parser.ast.TocPlaceholder
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.LocalMarkdownModifiers
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * 块级节点分发器。
 *
 * 每个 case 只负责把节点交给对应的 block renderer，并把 [LocalMarkdownModifiers]
 * 中对应元素的外部 Modifier 透传给它。
 * 内部不会追加任何 fillMaxWidth / padding 等强制布局修饰，全部由调用方通过
 * [com.hrm.markdown.ui.theme.MarkdownElementModifiers] 注入。
 */
@Composable
fun BlockRenderer(node: Node) {
    val modifiers = LocalMarkdownModifiers.current
    val extensions = LocalMarkdownExtensionProvider.current
    val theme = LocalMarkdownTheme.current

    when (node) {
        is Heading -> HeadingRenderer(node, modifiers.heading)
        is SetextHeading -> SetextHeadingRenderer(node, modifiers.heading)
        is Paragraph -> ParagraphRenderer(node, modifiers.paragraph)
        is ThematicBreak -> ThematicBreakRenderer(modifiers.thematicBreak)
        is FencedCodeBlock -> FencedCodeBlockRenderer(node, modifiers.codeBlock)
        is IndentedCodeBlock -> IndentedCodeBlockRenderer(node, modifiers.codeBlock)
        is BlockQuote -> BlockQuoteRenderer(node, modifiers.blockQuote)
        is ListBlock -> ListBlockRenderer(node, modifiers.list)
        is Table -> TableRenderer(node, modifiers.table)
        is HtmlBlock -> HtmlBlockRenderer(node, modifiers.htmlBlock)
        is Admonition -> AdmonitionRenderer(node, modifiers.admonition)
        is DefinitionList -> DefinitionListRenderer(node, modifiers.definitionList)
        is FootnoteDefinition -> FootnoteDefinitionRenderer(node, modifiers.footnoteDefinition)
        is PageBreak -> ThematicBreakRenderer(modifiers.thematicBreak)
        is Figure -> extensions.Figure(node, theme.figure, modifiers.figure)
        is MathBlock -> extensions.MathBlock(node, theme.math, modifiers.math)
        is DiagramBlock -> extensions.Diagram(node, theme.codeBlock, modifiers.diagram)
        is CustomContainer -> extensions.CustomContainer(
            node = node,
            theme = theme,
            modifier = modifiers.customContainer,
            renderContent = { MarkdownBlockChildren(node) },
        )
        is ShortcodeBlock -> extensions.ShortcodeBlock(
            node = node,
            theme = theme,
            modifier = modifiers.shortcode,
            renderContent = { MarkdownBlockChildren(node) },
        )
        is TabBlock -> extensions.TabBlock(node, theme, modifiers.tabBlock)
        is ColumnsLayout -> ColumnsLayoutRenderer(node, modifiers.columns)
        is BibliographyDefinition -> BibliographyDefinitionRenderer(node, modifiers.footnoteDefinition)
        is TocPlaceholder -> TocPlaceholderRenderer(node)
        is FrontMatter, is BlankLine, is LinkReferenceDefinition, is AbbreviationDefinition -> Unit
        else -> {
            // 未知块级节点：退化为容器遍历，最大化兼容性。
            if (node is ContainerNode) {
                for (child in node.children) BlockRenderer(child)
            }
        }
    }
}

/**
 * 渲染一个 [ContainerNode] 内部的块级子节点序列，使用 theme 的 blockSpacing。
 *
 * 这是 BlockQuote、ListItem、CustomContainer 等容器节点内部复用的基础构件。
 */
@Composable
fun MarkdownBlockChildren(
    parent: ContainerNode,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(theme.document.blockSpacing),
    ) {
        for (child in parent.children) {
            if (child is BlankLine) continue
            key(child::class, child.stableKey) {
                BlockRenderer(child)
            }
        }
    }
}
