package com.hrm.markdown.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.hrm.markdown.parser.ast.BibliographyDefinition
import com.hrm.markdown.ui.theme.LocalMarkdownTheme

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
            .clip(RoundedCornerShape(theme.bibliographyCornerRadius))
            .background(theme.bibliographyBackground)
            .padding(theme.bibliographyPadding),
        verticalArrangement = Arrangement.spacedBy(theme.bibliographyItemSpacing),
    ) {
        BasicText(
            text = "References",
            style = theme.bibliographyTitleTextStyle ?: theme.headingStyles.getOrElse(3) { theme.bodyStyle }.copy(
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(bottom = theme.bibliographyTitleBottomPadding),
        )

        node.entries.entries.forEachIndexed { index, (key, entry) ->
            Row(
                modifier = Modifier
                    .padding(theme.bibliographyItemPadding),
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
