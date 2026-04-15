package com.hrm.markdown.ui.block

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.CustomContainer
import com.hrm.markdown.ui.LocalMarkdownExtensionProvider
import com.hrm.markdown.ui.MarkdownBlockChildren
import com.hrm.markdown.ui.theme.LocalMarkdownTheme

/**
 * 自定义容器渲染器 (::: type ... :::)。
 *
 * 如果容器类型匹配已知的 Admonition 样式（NOTE/TIP/WARNING 等），
 * 则使用 Admonition 风格渲染；否则使用通用容器样式。
 */
@Composable
internal fun CustomContainerRenderer(
    node: CustomContainer,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    LocalMarkdownExtensionProvider.current.CustomContainer(
        node = node,
        theme = theme,
        modifier = modifier,
    ) {
        MarkdownBlockChildren(parent = node)
    }
}
