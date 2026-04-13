package com.hrm.markdown.ui.block

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * 分页符渲染器：`***pagebreak***`。
 *
 * 在屏幕预览中以虚线 + 标签形式展示分页位置。
 * PDF 导出/打印场景下，渲染器可替换为实际分页样式。
 */
@Composable
internal fun PageBreakRenderer(
    modifier: Modifier = Modifier,
) {
    LocalMarkdownExtensionProvider.current.PageBreak(
        theme = LocalMarkdownTheme.current,
        modifier = modifier,
    )
}
