package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hrm.markdown.parser.ast.ColumnItem
import com.hrm.markdown.parser.ast.ColumnsLayout

/**
 * 多列布局渲染器。
 *
 * 每列按 `width` 字段解析为 [RowScope.weight]，空字符串走平均分配。
 */
@Composable
internal fun ColumnsLayoutRenderer(node: ColumnsLayout, modifier: Modifier = Modifier) {
    val columns = node.children.filterIsInstance<ColumnItem>()
    if (columns.isEmpty()) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (column in columns) {
            val weight = parseWeight(column.width)
            MarkdownBlockChildren(
                parent = column,
                modifier = Modifier.weight(weight),
            )
        }
    }
}

private fun parseWeight(width: String): Float {
    if (width.isBlank()) return 1f
    val match = PERCENT_REGEX.find(width) ?: return 1f
    val percent = match.groupValues[1].toFloatOrNull() ?: return 1f
    if (percent <= 0f) return 1f
    return (percent / 100f).coerceIn(0.05f, 1f)
}

private val PERCENT_REGEX = Regex("""^(\d+(?:\.\d+)?)%$""")
