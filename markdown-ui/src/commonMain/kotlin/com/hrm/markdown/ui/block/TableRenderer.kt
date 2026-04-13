package com.hrm.markdown.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import com.hrm.markdown.parser.ast.Table
import com.hrm.markdown.parser.ast.TableBody
import com.hrm.markdown.parser.ast.TableCell
import com.hrm.markdown.parser.ast.TableHead
import com.hrm.markdown.parser.ast.TableRow
import com.hrm.markdown.ui.LocalMarkdownModifiers
import com.hrm.markdown.ui.LocalMarkdownTheme
import com.hrm.markdown.ui.inline.rememberInlineContent

/**
 * GFM 表格渲染器。
 *
 * 使用自定义 Layout 测算每列最大内容宽度，再按该宽度放置单元格，
 * 使所有行的同一列保持对齐。外层始终支持水平滚动，
 * 单元格不再被迫 `fillMaxWidth`。
 */
@Composable
internal fun TableRenderer(node: Table, modifier: Modifier = Modifier) {
    val theme = LocalMarkdownTheme.current
    val columnCount = node.columnAlignments.size.coerceAtLeast(1)

    val allRows = mutableListOf<Pair<TableRow, Boolean>>()
    node.children.filterIsInstance<TableHead>().firstOrNull()
        ?.children?.filterIsInstance<TableRow>()
        ?.forEach { allRows += it to true }
    node.children.filterIsInstance<TableBody>().firstOrNull()
        ?.children?.filterIsInstance<TableRow>()
        ?.forEach { allRows += it to false }

    Box(modifier = modifier.horizontalScroll(rememberScrollState())) {
        TableLayout(
            allRows = allRows,
            alignments = node.columnAlignments,
            columnCount = columnCount,
            modifier = Modifier.border(
                width = theme.table.borderWidth,
                color = theme.table.borderColor,
            ),
        )
    }
}

@Composable
private fun TableLayout(
    allRows: List<Pair<TableRow, Boolean>>,
    alignments: List<Table.Alignment>,
    columnCount: Int,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val tableStyle = theme.table
    val cellModifier = LocalMarkdownModifiers.current.tableCell

    Layout(
        modifier = modifier,
        content = {
            for ((row, isHeader) in allRows) {
                val cells = row.children.filterIsInstance<TableCell>()
                for (colIndex in 0 until columnCount) {
                    val cell = cells.getOrNull(colIndex)
                    val alignment = alignments.getOrElse(colIndex) { Table.Alignment.NONE }
                    val bgMod = if (isHeader) {
                        Modifier.background(tableStyle.headerBackground)
                    } else Modifier
                    TableCellRenderer(
                        cell = cell,
                        alignment = alignment,
                        isHeader = isHeader,
                        modifier = bgMod
                            .border(tableStyle.cellBorderWidth, tableStyle.borderColor)
                            .padding(tableStyle.cellPadding)
                            .then(cellModifier),
                    )
                }
            }
        },
    ) { measurables, _ ->
        val rowCount = allRows.size
        if (rowCount == 0 || columnCount == 0) {
            return@Layout layout(0, 0) {}
        }

        val columnWidths = IntArray(columnCount)
        for (index in measurables.indices) {
            val colIdx = index % columnCount
            val intrinsic = measurables[index].maxIntrinsicWidth(Constraints.Infinity)
            columnWidths[colIdx] = maxOf(columnWidths[colIdx], intrinsic)
        }

        val placeables = Array(measurables.size) { index ->
            val colIdx = index % columnCount
            val fixed = columnWidths[colIdx]
            measurables[index].measure(Constraints(minWidth = fixed, maxWidth = fixed))
        }

        val rowHeights = IntArray(rowCount)
        for (rowIdx in 0 until rowCount) {
            for (colIdx in 0 until columnCount) {
                val p = placeables[rowIdx * columnCount + colIdx]
                rowHeights[rowIdx] = maxOf(rowHeights[rowIdx], p.height)
            }
        }

        val totalWidth = columnWidths.sum()
        val totalHeight = rowHeights.sum()

        layout(totalWidth, totalHeight) {
            var y = 0
            for (rowIdx in 0 until rowCount) {
                var x = 0
                for (colIdx in 0 until columnCount) {
                    val p = placeables[rowIdx * columnCount + colIdx]
                    p.placeRelative(x, y)
                    x += columnWidths[colIdx]
                }
                y += rowHeights[rowIdx]
            }
        }
    }
}

@Composable
private fun TableCellRenderer(
    cell: TableCell?,
    alignment: Table.Alignment,
    isHeader: Boolean,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val tableStyle = theme.table

    val textAlign = when (alignment) {
        Table.Alignment.LEFT -> TextAlign.Start
        Table.Alignment.CENTER -> TextAlign.Center
        Table.Alignment.RIGHT -> TextAlign.End
        Table.Alignment.NONE -> TextAlign.Start
    }

    val style = if (isHeader) {
        tableStyle.headerTextStyle.copy(textAlign = textAlign)
    } else {
        tableStyle.cellTextStyle.copy(textAlign = textAlign)
    }

    if (cell == null) {
        Box(modifier = modifier)
        return
    }

    val (annotated, inlineContents) = rememberInlineContent(cell)

    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        BasicText(
            text = annotated,
            style = style,
            inlineContent = inlineContents,
            maxLines = 1,
        )
    }
}
