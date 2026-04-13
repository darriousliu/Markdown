package com.hrm.markdown.ui.extension

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hrm.markdown.parser.ast.CustomContainer
import com.hrm.markdown.parser.ast.DiagramBlock
import com.hrm.markdown.parser.ast.FencedCodeBlock
import com.hrm.markdown.parser.ast.Figure
import com.hrm.markdown.parser.ast.Image
import com.hrm.markdown.parser.ast.IndentedCodeBlock
import com.hrm.markdown.parser.ast.MathBlock
import com.hrm.markdown.parser.ast.ShortcodeBlock
import com.hrm.markdown.parser.ast.TabBlock
import com.hrm.markdown.parser.ast.TabItem
import com.hrm.markdown.ui.diagram.DiagramFallback
import com.hrm.markdown.ui.diagram.GraphvizDiagram
import com.hrm.markdown.ui.diagram.MermaidFlowchartDiagram
import com.hrm.markdown.ui.diagram.MermaidSequenceDiagram
import com.hrm.markdown.ui.diagram.PlantUMLSequenceDiagram
import com.hrm.markdown.ui.theme.CodeBlockStyle
import com.hrm.markdown.ui.theme.FigureStyle
import com.hrm.markdown.ui.theme.ImageStyle
import com.hrm.markdown.ui.theme.MarkdownTheme
import com.hrm.markdown.ui.theme.MathStyle
import com.hrm.markdown.ui.theme.ThematicBreakStyle

/**
 * 扩展节点与可替换 UI 片段的默认实现。
 *
 * 默认实现尽量保持纯净：
 * - 遇到三方 UI 组件（LaTeX、代码高亮、图片加载）时退化为 `BasicText`
 * - 非三方、模块内已有的基础图形渲染可以直接复用
 */
open class DefaultExtensionProvider : MarkdownExtensionProvider {

    @Composable
    override fun FencedCodeBlock(
        node: FencedCodeBlock,
        style: CodeBlockStyle,
        modifier: Modifier,
    ) {
        BasicCodeBlock(
            text = buildCodeBlockText(
                title = node.attributes.pairs["title"],
                language = node.language,
                content = node.literal,
            ),
            style = style,
            modifier = modifier,
        )
    }

    @Composable
    override fun IndentedCodeBlock(
        node: IndentedCodeBlock,
        style: CodeBlockStyle,
        modifier: Modifier,
    ) {
        BasicCodeBlock(
            text = node.literal,
            style = style,
            modifier = modifier,
        )
    }

    @Composable
    override fun HorizontalDivider(
        style: ThematicBreakStyle,
        modifier: Modifier,
    ) {
        Box(
            modifier = modifier
                .height(style.thickness)
                .background(style.color)
        )
    }

    @Composable
    override fun PageBreak(
        theme: MarkdownTheme,
        modifier: Modifier,
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HorizontalDivider(theme.thematicBreak, Modifier)
            BasicText(
                text = "Page Break",
                style = theme.paragraph.textStyle.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
            HorizontalDivider(theme.thematicBreak, Modifier)
        }
    }

    @Composable
    override fun MathBlock(
        node: MathBlock,
        style: MathStyle,
        modifier: Modifier,
    ) {
        BasicText(
            text = node.literal,
            modifier = modifier
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(style.cornerRadius))
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
    override fun Diagram(
        node: DiagramBlock,
        style: CodeBlockStyle,
        modifier: Modifier,
    ) {
        val code = node.literal.trimEnd('\n')
        val diagramType = node.diagramType.lowercase()

        Column(
            modifier = modifier
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(style.cornerRadius))
                .background(style.background)
                .padding(style.padding),
        ) {
            when {
                diagramType == "mermaid" -> {
                    val firstLine = code.lines().firstOrNull()?.trim()?.lowercase().orEmpty()
                    when {
                        firstLine.startsWith("flowchart") || firstLine.startsWith("graph") -> {
                            MermaidFlowchartDiagram(code)
                        }
                        firstLine.startsWith("sequencediagram") || firstLine.startsWith("sequence") -> {
                            MermaidSequenceDiagram(code)
                        }
                        else -> MermaidFlowchartDiagram(code)
                    }
                }
                diagramType == "plantuml" -> PlantUMLSequenceDiagram(code)
                diagramType in setOf("dot", "graphviz") -> GraphvizDiagram(code)
                else -> {
                    val typeName = node.diagramType.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else it.toString()
                    }
                    DiagramFallback(code, typeName)
                }
            }
        }
    }

    @Composable
    override fun BlockImage(
        node: Image,
        altText: String,
        style: ImageStyle,
        modifier: Modifier,
    ) {
        BasicText(
            text = altText.ifEmpty { node.destination },
            modifier = modifier,
            style = style.captionTextStyle,
        )
    }

    @Composable
    override fun Figure(
        node: Figure,
        style: FigureStyle,
        modifier: Modifier,
    ) {
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
    override fun CustomContainer(
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
    override fun ShortcodeBlock(
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
    override fun TabBlock(
        node: TabBlock,
        theme: MarkdownTheme,
        modifier: Modifier,
    ) {
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

    companion object {
        val Default: DefaultExtensionProvider = DefaultExtensionProvider()
    }
}

@Composable
private fun BasicCodeBlock(
    text: String,
    style: CodeBlockStyle,
    modifier: Modifier,
) {
    BasicText(
        text = text.ifEmpty { " " },
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(style.cornerRadius))
            .background(style.background)
            .padding(style.padding),
        style = style.textStyle,
    )
}

private fun buildCodeBlockText(
    title: String?,
    language: String,
    content: String,
): String = buildString {
    if (!title.isNullOrEmpty()) {
        append(title)
        append('\n')
    }
    if (language.isNotEmpty()) {
        append("```")
        append(language)
        append('\n')
    }
    append(content)
    if (language.isNotEmpty()) {
        append('\n')
        append("```")
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
