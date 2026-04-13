package com.hrm.markdown.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hrm.markdown.parser.ast.BibliographyDefinition
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * 参考文献定义渲染器。
 */
@Composable
internal fun BibliographyDefinitionRenderer(
    node: BibliographyDefinition,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    if (node.entries.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(theme.codeBlockBackground)
            .padding(12.dp),
    ) {
        BasicText(
            text = "References",
            style = theme.headingStyles.getOrElse(3) { theme.bodyStyle }.copy(
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(bottom = 8.dp),
        )

        node.entries.entries.forEachIndexed { index, (key, entry) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
            ) {
                BasicText(
                    text = "[${key}] ",
                    style = theme.bodyStyle.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = theme.linkColor,
                    ),
                )
                BasicText(
                    text = entry.content,
                    style = theme.bodyStyle,
                )
            }
        }
    }
}
