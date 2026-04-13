package com.hrm.markdown.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hrm.markdown.parser.ast.BibliographyDefinition
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * 参考文献块渲染器。
 *
 * 以 `References` 作为标题，列出所有 `BibEntry`。样式沿用代码块背景色作为区分。
 */
@Composable
internal fun BibliographyDefinitionRenderer(
    node: BibliographyDefinition,
    modifier: Modifier = Modifier,
) {
    if (node.entries.isEmpty()) return
    val theme = LocalMarkdownTheme.current

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(theme.codeBlock.background)
            .padding(12.dp),
    ) {
        BasicText(
            text = "References",
            style = theme.heading.textStyles.getOrElse(3) { theme.paragraph.textStyle }
                .copy(fontWeight = theme.strongEmphasis.textStyle.fontWeight),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        for ((key, entry) in node.entries) {
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                BasicText(
                    text = "[$key] ",
                    style = theme.paragraph.textStyle.copy(
                        color = theme.link.textStyle.color,
                        fontWeight = theme.strongEmphasis.textStyle.fontWeight,
                    ),
                )
                BasicText(text = entry.content, style = theme.paragraph.textStyle)
            }
        }
    }
}
