package com.hrm.markdown.ui

import androidx.compose.runtime.Immutable
import com.hrm.markdown.parser.flavour.ExtendedFlavour
import com.hrm.markdown.parser.flavour.MarkdownFlavour

/**
 * Parser 层配置，控制方言与附加处理行为。
 *
 * 该 config 对象只影响底层 [com.hrm.markdown.parser.MarkdownParser] 的构造，
 * 不包含任何 UI / 主题相关参数。
 */
@Immutable
data class MarkdownUiConfig(
    val flavour: MarkdownFlavour = ExtendedFlavour,
    val customEmojiMap: Map<String, String> = emptyMap(),
    val enableAsciiEmoticons: Boolean = false,
    val enableLinting: Boolean = false,
    /**
     * 标题自动编号：1、1.1、1.1.1…
     * 开启后 renderer 会基于文档顺序生成编号并加在标题之前。
     */
    val enableHeadingNumbering: Boolean = false,
) {
    companion object {
        val Default = MarkdownUiConfig()
    }
}
