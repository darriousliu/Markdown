package com.hrm.markdown.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier

/**
 * 各 Markdown 元素的外部 Modifier 注入点。
 *
 * 渲染内部只会把这些 Modifier 原样 `then` 到元素的外层容器上，
 * 不会强加 `fillMaxWidth`、`padding` 等业务布局修饰。
 *
 * 这样外部调用者可以完全掌控每个元素的布局尺寸、背景、点击事件等，
 * 既能满足商业项目的自定义需求，又不污染内部实现。
 */
@Immutable
data class MarkdownElementModifiers(
    val document: Modifier = Modifier,
    val heading: Modifier = Modifier,
    val paragraph: Modifier = Modifier,
    val blockQuote: Modifier = Modifier,
    val thematicBreak: Modifier = Modifier,
    val pageBreak: Modifier = Modifier,
    val codeBlock: Modifier = Modifier,
    val htmlBlock: Modifier = Modifier,
    val list: Modifier = Modifier,
    val listItem: Modifier = Modifier,
    val table: Modifier = Modifier,
    val tableCell: Modifier = Modifier,
    val math: Modifier = Modifier,
    val image: Modifier = Modifier,
    val figure: Modifier = Modifier,
    val diagram: Modifier = Modifier,
    val customContainer: Modifier = Modifier,
    val shortcode: Modifier = Modifier,
    val admonition: Modifier = Modifier,
    val definitionList: Modifier = Modifier,
    val footnoteDefinition: Modifier = Modifier,
    val tabBlock: Modifier = Modifier,
    val columns: Modifier = Modifier,
) {
    companion object {
        val None = MarkdownElementModifiers()
    }
}
