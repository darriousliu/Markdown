package com.hrm.markdown.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hrm.markdown.parser.ast.BlankLine
import com.hrm.markdown.parser.ast.ListBlock
import com.hrm.markdown.parser.ast.ListItem
import com.hrm.markdown.parser.ast.Paragraph
import com.hrm.markdown.ui.LocalMarkdownTheme
import com.hrm.markdown.ui.MarkdownBlockChildren

/**
 * 列表渲染器（有序/无序列表）。
 */
@Composable
internal fun ListBlockRenderer(
    node: ListBlock,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val spacing = if (node.tight) theme.list.tightSpacing else theme.list.looseSpacing

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        val items = node.children.filterIsInstance<ListItem>()
        items.forEachIndexed { index, item ->
            ListItemRenderer(
                node = item,
                ordered = node.ordered,
                index = node.startNumber + index,
            )
        }
    }
}

@Composable
private fun ListItemRenderer(
    node: ListItem,
    ordered: Boolean,
    index: Int,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current

    Row(
        modifier = modifier.padding(start = theme.listIndent),
        horizontalArrangement = Arrangement.Start,
    ) {
        // Marker 列
        when {
            node.taskListItem -> {
                TaskListMarker(
                    checked = node.checked,
                    modifier = Modifier.align(Alignment.Top),
                )
            }
            ordered -> {
                BasicText(
                    text = "${index}.",
                    modifier = Modifier.width(24.dp).align(Alignment.Top),
                    style = theme.bodyStyle.copy(
                        color = theme.listBulletColor,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            else -> {
                BasicText(
                    text = theme.list.bullet,
                    modifier = Modifier.width(theme.list.markerWidth).align(Alignment.Top),
                    style = theme.bodyStyle.copy(color = theme.listBulletColor),
                )
            }
        }

        // 内容列
        Box(modifier = Modifier.weight(1f)) {
            MarkdownBlockChildren(node)
        }
    }
}

@Composable
private fun TaskListMarker(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    Box(
        modifier = modifier
            .size(theme.taskList.boxSize)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
            .background(
                color = if (checked) theme.taskList.checkedColor else androidx.compose.ui.graphics.Color.Transparent,
            )
            .border(
                width = theme.taskList.strokeWidth,
                brush = SolidColor(if (checked) theme.taskList.checkedColor else theme.taskList.uncheckedColor),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            BasicText(
                text = "✓",
                style = theme.bodyStyle.copy(
                    color = theme.taskList.checkmarkColor,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}
