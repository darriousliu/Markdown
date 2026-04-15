package com.hrm.markdown.ui.block

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.DiagramBlock
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.theme.LocalMarkdownTheme

/**
 * 图表块渲染器（Mermaid / PlantUML 等）。
 *
 * 根据图表类型分发到对应的渲染引擎：
 * - Mermaid flowchart/graph → Canvas 绘制流程图
 * - PlantUML sequence → Canvas 绘制时序图
 * - 其他未支持类型 → 代码展示 fallback
 */
@Composable
internal fun DiagramBlockRenderer(
    node: DiagramBlock,
    modifier: Modifier = Modifier,
) {
    LocalMarkdownExtensionProvider.current.Diagram(
        node = node,
        style = LocalMarkdownTheme.current.codeBlock,
        modifier = modifier,
    )
}
