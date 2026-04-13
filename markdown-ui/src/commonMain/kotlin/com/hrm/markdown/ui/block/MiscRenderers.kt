package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hrm.markdown.parser.ast.DefinitionDescription
import com.hrm.markdown.parser.ast.DefinitionList
import com.hrm.markdown.parser.ast.DefinitionTerm
import com.hrm.markdown.parser.ast.FootnoteDefinition
import com.hrm.markdown.parser.ast.HtmlBlock
import com.hrm.markdown.ui.LocalMarkdownTheme
import com.hrm.markdown.ui.MarkdownBlockChildren
import com.hrm.markdown.ui.inline.rememberInlineContent

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
        modifier = modifier.fillMaxWidth(),
        style = theme.codeBlockStyle.copy(fontFamily = FontFamily.Monospace),
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
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        for (child in node.children) {
            when (child) {
                is DefinitionTerm -> {
                    val (annotated, _) = rememberInlineContent(child)
                    BasicText(
                        text = annotated,
                        style = theme.bodyStyle.copy(fontWeight = FontWeight.Bold),
                    )
                }
                is DefinitionDescription -> {
                    MarkdownBlockChildren(
                        parent = child,
                        modifier = Modifier.padding(start = 24.dp),
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

    Column(modifier = modifier.fillMaxWidth().padding(top = 4.dp)) {
        BasicText(
            text = "[${node.index}] ${node.label}",
            style = theme.bodyStyle.copy(
                fontWeight = FontWeight.SemiBold,
                    fontSize = theme.footnoteStyle.fontSize,
            ),
        )
        MarkdownBlockChildren(
            parent = node,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}
