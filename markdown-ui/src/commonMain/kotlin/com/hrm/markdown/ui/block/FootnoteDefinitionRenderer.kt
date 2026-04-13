package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.hrm.markdown.parser.ast.FootnoteDefinition
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * 脚注定义渲染器：顶部展示 `[index] label`，内容块以缩进方式呈现。
 */
@Composable
internal fun FootnoteDefinitionRenderer(node: FootnoteDefinition, modifier: Modifier = Modifier) {
    val theme = LocalMarkdownTheme.current
    val footnote = theme.footnote

    Column(modifier = modifier.padding(top = 4.dp)) {
        BasicText(
            text = "[${node.index}] ${node.label}",
            style = TextStyle(
                fontSize = footnote.textStyle.fontSize,
                fontWeight = theme.strongEmphasis.textStyle.fontWeight,
                color = footnote.textStyle.color,
            ),
        )
        MarkdownBlockChildren(
            parent = node,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}
