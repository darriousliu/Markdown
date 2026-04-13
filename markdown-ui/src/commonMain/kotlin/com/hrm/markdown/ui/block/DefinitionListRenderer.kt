package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.DefinitionDescription
import com.hrm.markdown.parser.ast.DefinitionList
import com.hrm.markdown.parser.ast.DefinitionTerm
import com.hrm.markdown.ui.LocalMarkdownTheme
import com.hrm.markdown.ui.inline.rememberInlineContent

/**
 * 定义列表渲染器。
 *
 * - term：行内渲染，字体加粗（来自 theme.definitionList.termTextStyle）
 * - description：作为嵌套块级内容，带 [com.hrm.markdown.ui.theme.DefinitionListStyle.descriptionIndent] 缩进
 */
@Composable
internal fun DefinitionListRenderer(node: DefinitionList, modifier: Modifier = Modifier) {
    val theme = LocalMarkdownTheme.current
    val style = theme.definitionList

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(style.itemSpacing),
    ) {
        for (child in node.children) {
            when (child) {
                is DefinitionTerm -> {
                    val (annotated, inlineContents) = rememberInlineContent(child)
                    BasicText(
                        text = annotated,
                        style = style.termTextStyle,
                        inlineContent = inlineContents,
                    )
                }
                is DefinitionDescription -> {
                    MarkdownBlockChildren(
                        parent = child,
                        modifier = Modifier.padding(start = style.descriptionIndent),
                    )
                }
                else -> BlockRenderer(child)
            }
        }
    }
}
