package com.hrm.markdown.ui.block

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import com.hrm.markdown.parser.ast.ListBlock
import com.hrm.markdown.parser.ast.ListItem
import com.hrm.markdown.ui.LocalMarkdownModifiers
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * 有序 / 无序列表渲染器。
 *
 * - tight 列表使用较小的块间距，loose 使用正常块间距。
 * - 不对外部容器施加 `fillMaxWidth`，布局由调用方 / 外部 Modifier 决定。
 */
@Composable
internal fun ListBlockRenderer(node: ListBlock, modifier: Modifier = Modifier) {
    val theme = LocalMarkdownTheme.current
    val listStyle = theme.list
    val spacing = if (node.tight) listStyle.tightSpacing else listStyle.looseSpacing

    val items = node.children.filterIsInstance<ListItem>()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        items.forEachIndexed { index, item ->
            ListItemRow(
                item = item,
                ordered = node.ordered,
                index = node.startNumber + index,
            )
        }
    }
}

@Composable
private fun ListItemRow(item: ListItem, ordered: Boolean, index: Int) {
    val theme = LocalMarkdownTheme.current
    val listStyle = theme.list
    val modifiers = LocalMarkdownModifiers.current

    Row(
        modifier = modifiers.listItem,
        verticalAlignment = Alignment.Top,
    ) {
        // marker 列
        when {
            item.taskListItem -> TaskListCheckbox(checked = item.checked)
            ordered -> BasicText(
                text = AnnotatedString("$index."),
                modifier = Modifier.width(listStyle.markerWidth),
                style = theme.paragraph.textStyle.copy(color = listStyle.bulletColor),
            )
            else -> BasicText(
                text = AnnotatedString(listStyle.bullet),
                modifier = Modifier.width(listStyle.markerWidth),
                style = theme.paragraph.textStyle.copy(color = listStyle.bulletColor),
            )
        }

        // 内容列
        Box {
            MarkdownBlockChildren(item)
        }
    }
}

/**
 * 自绘任务列表复选框，不引入 Material3。
 *
 * 勾选时填充背景并绘制白色对勾，未勾选时仅绘制边框。
 */
@Composable
private fun TaskListCheckbox(checked: Boolean) {
    val theme = LocalMarkdownTheme.current
    val listStyle = theme.list
    val style = theme.taskList

    Canvas(modifier = Modifier.width(listStyle.markerWidth).size(listStyle.markerWidth)) {
        val box = style.boxSize.toPx()
        val inset = (size.width - box) / 2f
        val topLeft = Offset(inset, inset)
        val strokePx = style.strokeWidth.toPx()

        if (checked) {
            drawRect(
                color = style.checkedColor,
                topLeft = topLeft,
                size = Size(box, box),
            )
            val path = Path().apply {
                moveTo(topLeft.x + box * 0.20f, topLeft.y + box * 0.55f)
                lineTo(topLeft.x + box * 0.45f, topLeft.y + box * 0.78f)
                lineTo(topLeft.x + box * 0.80f, topLeft.y + box * 0.28f)
            }
            drawPath(path = path, color = style.checkmarkColor, style = Stroke(width = strokePx))
        } else {
            drawRect(
                color = style.uncheckedColor,
                topLeft = topLeft,
                size = Size(box, box),
                style = Stroke(width = strokePx),
            )
        }
    }
}
