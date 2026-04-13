package com.hrm.markdown.ui

import androidx.compose.runtime.Immutable
import com.hrm.markdown.parser.flavour.ExtendedFlavour
import com.hrm.markdown.parser.flavour.MarkdownFlavour

/**
 * Markdown 渲染器的解析配置。
 *
 * 将解析器的配置选项暴露给渲染层，允许外部控制使用的 Markdown 方言和解析行为。
 *
 * ## 使用示例
 *
 * ```kotlin
 * // 默认配置（ExtendedFlavour，全功能）
 * Markdown(markdown = "# Hello")
 *
 * // 使用 GFM 方言
 * Markdown(
 *     markdown = "# Hello",
 *     config = MarkdownConfig(flavour = GFMFlavour),
 * )
 *
 * // 使用 CommonMark 方言
 * Markdown(
 *     markdown = "# Hello",
 *     config = MarkdownConfig(flavour = CommonMarkFlavour),
 * )
 *
 * // 自定义方言 + Emoji
 * Markdown(
 *     markdown = ":myemoji: Hello",
 *     config = MarkdownConfig(
 *         flavour = ExtendedFlavour,
 *         customEmojiMap = mapOf("myemoji" to "🎉"),
 *         enableAsciiEmoticons = true,
 *     ),
 * )
 * ```
 *
 * // 启用标题自动编号
 * Markdown(
 *     markdown = "# Intro\n## Section\n## Section 2",
 *     config = MarkdownConfig(enableHeadingNumbering = true),
 * )
 * ```
 *
 * @param flavour Markdown 方言，控制支持的语法特性。默认为 [ExtendedFlavour]（包含所有扩展）。
 * @param customEmojiMap 自定义 Emoji 别名映射（shortcode → unicode），默认为空。
 * @param enableAsciiEmoticons 是否启用 ASCII 表情自动转换（如 `:)` → 😊），默认关闭。
 * @param enableLinting 是否启用语法验证/Linting，默认关闭。
 * @param enableHeadingNumbering 是否启用标题自动编号（如 1, 1.1, 1.1.1），默认关闭。
 */
@Immutable
data class MarkdownConfig(
    val flavour: MarkdownFlavour = ExtendedFlavour,
    val customEmojiMap: Map<String, String> = emptyMap(),
    val enableAsciiEmoticons: Boolean = false,
    val enableLinting: Boolean = false,
    val enableHeadingNumbering: Boolean = false,
) {
    companion object {
        /** 默认配置：ExtendedFlavour，全功能。 */
        val Default = MarkdownConfig()
    }
}
