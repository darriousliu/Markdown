package com.hrm.markdown.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.hrm.markdown.parser.ast.Document
import com.hrm.markdown.ui.extension.MarkdownExtensionProvider
import com.hrm.markdown.ui.theme.MarkdownElementModifiers
import com.hrm.markdown.ui.theme.MarkdownTheme

/** 当前生效的 [MarkdownTheme]。 */
val LocalMarkdownTheme = staticCompositionLocalOf { MarkdownTheme.Light }

/** 每个元素的外部 Modifier。 */
val LocalMarkdownModifiers = staticCompositionLocalOf { MarkdownElementModifiers.None }

/** 扩展节点 Provider。 */
val LocalMarkdownExtensionProvider = staticCompositionLocalOf<MarkdownExtensionProvider> {
    MarkdownExtensionProvider.None
}

/** 链接点击回调。 */
val LocalMarkdownLinkHandler = compositionLocalOf<((String) -> Unit)?> { null }

/**
 * 当前 Document 引用（非 static，避免对不关心 document 的子树造成无效重组）。
 * 内部 TOC 等少量需要访问整篇文档的渲染器会读取它。
 */
val LocalMarkdownDocument = compositionLocalOf { Document() }

/** 当前是否处于流式渲染阶段（供内部渲染器做节流 / 去选择优化使用）。 */
val LocalMarkdownIsStreaming = staticCompositionLocalOf { false }

/** 是否启用标题自动编号。 */
val LocalMarkdownHeadingNumbering = staticCompositionLocalOf { false }

/**
 * 一次性提供所有 markdown-ui 相关的 CompositionLocal。
 *
 * 顶层 [com.hrm.markdown.ui.Markdown] 已自动完成注入；
 * 如果调用方只想拿 Document 自己绘制 UI，也可以直接使用这个 Provider。
 */
@Composable
fun ProvideMarkdownUi(
    theme: MarkdownTheme = MarkdownTheme.Light,
    modifiers: MarkdownElementModifiers = MarkdownElementModifiers.None,
    extensionProvider: MarkdownExtensionProvider = MarkdownExtensionProvider.None,
    onLinkClick: ((String) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalMarkdownTheme provides theme,
        LocalMarkdownModifiers provides modifiers,
        LocalMarkdownExtensionProvider provides extensionProvider,
        LocalMarkdownLinkHandler provides onLinkClick,
    ) {
        content()
    }
}
