package com.hrm.markdown.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.hrm.markdown.parser.ast.DefinitionDescription
import com.hrm.markdown.parser.ast.DefinitionList
import com.hrm.markdown.parser.ast.DefinitionTerm
import com.hrm.markdown.parser.ast.FootnoteDefinition
import com.hrm.markdown.parser.ast.HtmlBlock
import com.hrm.markdown.ui.MarkdownBlockChildren
import com.hrm.markdown.ui.inline.rememberInlineContent
import com.hrm.markdown.ui.theme.LocalMarkdownTheme

/**
 * HTML 块渲染器：以等宽字体显示原始 HTML。
 */
@Composable
internal fun HtmlBlockRenderer(
    node: HtmlBlock,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    BasicText(
        text = node.literal.trimEnd('\n'),
        modifier = modifier
            .clip(RoundedCornerShape(theme.htmlBlockCornerRadius))
            .background(theme.htmlBlockBackground)
            .padding(theme.htmlBlockPadding),
        style = theme.htmlBlockStyle,
    )
}

/**
 * 定义列表渲染器。
 */
@Composable
internal fun DefinitionListRenderer(
    node: DefinitionList,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(theme.definitionListItemSpacing),
    ) {
        for (child in node.children) {
            when (child) {
                is DefinitionTerm -> {
                    val (annotated, _) = rememberInlineContent(child)
                    BasicText(
                        text = annotated,
                        style = theme.definitionListTermTextStyle,
                    )
                }

                is DefinitionDescription -> {
                    MarkdownBlockChildren(
                        parent = child,
                        modifier = Modifier.padding(start = theme.definitionListDescriptionIndent),
                    )
                }

                else -> BlockRenderer(child)
            }
        }
    }
}

/**
 * 脚注定义渲染器。
 */
@Composable
internal fun FootnoteDefinitionRenderer(
    node: FootnoteDefinition,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current

    Column(modifier = modifier.padding(theme.footnoteDefinitionPadding)) {
        BasicText(
            text = "[${node.index}] ${node.label}",
            style = theme.footnoteDefinitionLabelStyle,
        )
        MarkdownBlockChildren(
            parent = node,
            modifier = Modifier.padding(start = theme.footnoteDefinitionIndent),
        )
    }
}
