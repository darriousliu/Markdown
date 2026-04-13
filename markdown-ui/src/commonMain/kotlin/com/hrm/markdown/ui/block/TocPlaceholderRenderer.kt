package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hrm.markdown.parser.ast.ContainerNode
import com.hrm.markdown.parser.ast.Document
import com.hrm.markdown.parser.ast.Heading
import com.hrm.markdown.parser.ast.Node
import com.hrm.markdown.parser.ast.SetextHeading
import com.hrm.markdown.parser.ast.TocPlaceholder
import com.hrm.markdown.ui.LocalMarkdownDocument
import com.hrm.markdown.ui.LocalMarkdownTheme
import com.hrm.markdown.ui.inline.extractPlainText

/**
 * Table of Contents 占位符渲染器。
 *
 * 遍历 [LocalMarkdownDocument] 中的所有标题节点，按 minDepth / maxDepth 及排除 ID 过滤，
 * 按 `order` 排序后绘制一个简单的目录列表。
 */
@Composable
internal fun TocPlaceholderRenderer(node: TocPlaceholder, modifier: Modifier = Modifier) {
    val theme = LocalMarkdownTheme.current
    val document = LocalMarkdownDocument.current

    var headings = collectHeadings(document)
    if (headings.isEmpty()) return

    headings = headings.filter { it.level in node.minDepth..node.maxDepth }
    if (node.excludeIds.isNotEmpty()) {
        headings = headings.filter { it.id == null || it.id !in node.excludeIds }
    }
    if (node.order == "desc") headings = headings.reversed()
    if (headings.isEmpty()) return

    Column(
        modifier = modifier.padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        BasicText(
            text = "Table of Contents",
            style = theme.heading.textStyles.getOrElse(2) { theme.paragraph.textStyle },
            modifier = Modifier.padding(bottom = 4.dp),
        )
        for ((text, level, _) in headings) {
            val adjusted = (level - node.minDepth).coerceAtLeast(0)
            BasicText(
                text = "${"  ".repeat(adjusted)}• $text",
                style = theme.paragraph.textStyle.copy(color = theme.link.textStyle.color),
                modifier = Modifier.padding(start = adjusted.dp * 12),
            )
        }
    }
}

private data class HeadingInfo(val text: String, val level: Int, val id: String?)

private fun collectHeadings(document: Document): List<HeadingInfo> {
    val result = mutableListOf<HeadingInfo>()
    for (child in document.children) collect(child, result)
    return result
}

private fun collect(node: Node, result: MutableList<HeadingInfo>) {
    when (node) {
        is Heading -> result += HeadingInfo(extractPlainText(node), node.level, node.id)
        is SetextHeading -> result += HeadingInfo(extractPlainText(node), node.level, node.id)
        is ContainerNode -> for (child in node.children) collect(child, result)
        else -> {}
    }
}
