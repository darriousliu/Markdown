package com.hrm.markdown.ui.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import com.hrm.markdown.parser.ast.CustomContainer
import com.hrm.markdown.parser.ast.DiagramBlock
import com.hrm.markdown.parser.ast.FencedCodeBlock
import com.hrm.markdown.parser.ast.Figure
import com.hrm.markdown.parser.ast.Image
import com.hrm.markdown.parser.ast.IndentedCodeBlock
import com.hrm.markdown.parser.ast.InlineMath
import com.hrm.markdown.parser.ast.MathBlock
import com.hrm.markdown.parser.ast.ShortcodeBlock
import com.hrm.markdown.parser.ast.ShortcodeInline
import com.hrm.markdown.parser.ast.TabBlock
import com.hrm.markdown.ui.theme.CodeBlockStyle
import com.hrm.markdown.ui.theme.FigureStyle
import com.hrm.markdown.ui.theme.ImageStyle
import com.hrm.markdown.ui.theme.MarkdownTheme
import com.hrm.markdown.ui.theme.MathStyle
import com.hrm.markdown.ui.theme.ThematicBreakStyle

/**
 * Flavor 扩展节点的外部实现入口。
 *
 * 仅用于 *扩展元素*（LaTeX、图表、图片等依赖平台 / 三方库的节点）。
 * 标准 Markdown 元素（标题、段落、列表、引用、代码块、表格等）
 * 由 markdown-ui 内部直接渲染，不经过本接口。
 *
 * 每个方法都提供了一个默认实现：
 * - 块级节点：默认以占位符 / 退化形式渲染（例如 Math 显示原始 LaTeX 文本）。
 * - 行内节点：默认返回 `null`，渲染器会降级为普通文本。
 *
 * 外部可只覆盖需要的槽位，其余保持默认行为。
 */
interface MarkdownExtensionProvider {

    @Composable
    fun FencedCodeBlock(
        node: FencedCodeBlock,
        style: CodeBlockStyle,
        modifier: Modifier,
    ) {
        DefaultExtensionProvider.Default.FencedCodeBlock(node, style, modifier)
    }

    @Composable
    fun IndentedCodeBlock(
        node: IndentedCodeBlock,
        style: CodeBlockStyle,
        modifier: Modifier,
    ) {
        DefaultExtensionProvider.Default.IndentedCodeBlock(node, style, modifier)
    }

    @Composable
    fun HorizontalDivider(
        style: ThematicBreakStyle,
        modifier: Modifier,
    ) {
        DefaultExtensionProvider.Default.HorizontalDivider(style, modifier)
    }

    @Composable
    fun PageBreak(
        theme: MarkdownTheme,
        modifier: Modifier,
    ) {
        DefaultExtensionProvider.Default.PageBreak(theme, modifier)
    }

    // ─────────── 块级扩展 ───────────

    /** 渲染块级数学公式（$$...$$）。 */
    @Composable
    fun MathBlock(
        node: MathBlock,
        style: MathStyle,
        modifier: Modifier,
    ) {
        DefaultExtensionProvider.Default.MathBlock(node, style, modifier)
    }

    /** 渲染图表代码块（mermaid / plantuml / graphviz ...）。 */
    @Composable
    fun Diagram(
        node: DiagramBlock,
        style: CodeBlockStyle,
        modifier: Modifier,
    ) {
        DefaultExtensionProvider.Default.Diagram(node, style, modifier)
    }

    /**
     * 渲染独立图片块（段落中唯一的图片会被提升为块级图片）。
     *
     * [altText] 为外部已经解析好的 alt 文字，便于调用方作为加载占位 /
     * accessibility 描述展示。
     */
    @Composable
    fun BlockImage(
        node: Image,
        altText: String,
        style: ImageStyle,
        modifier: Modifier,
    ) {
        DefaultExtensionProvider.Default.BlockImage(node, altText, style, modifier)
    }

    /** 渲染 Figure 节点（带 caption 的图片）。 */
    @Composable
    fun Figure(
        node: Figure,
        style: FigureStyle,
        modifier: Modifier,
    ) {
        DefaultExtensionProvider.Default.Figure(node, style, modifier)
    }

    /**
     * 渲染自定义容器块（`:::type ... :::`）。
     *
     * [renderContent] 是 renderer 预构建好的内部子块渲染函数，
     * 直接调用即可让标准 Markdown 元素按主题渲染，不需要外部重复实现子节点逻辑。
     */
    @Composable
    fun CustomContainer(
        node: CustomContainer,
        theme: MarkdownTheme,
        modifier: Modifier,
        renderContent: @Composable () -> Unit,
    ) {
        DefaultExtensionProvider.Default.CustomContainer(node, theme, modifier, renderContent)
    }

    /** 渲染块级 shortcode（`{% tag %} ... {% endtag %}`）。 */
    @Composable
    fun ShortcodeBlock(
        node: ShortcodeBlock,
        theme: MarkdownTheme,
        modifier: Modifier,
        renderContent: @Composable () -> Unit,
    ) {
        DefaultExtensionProvider.Default.ShortcodeBlock(node, theme, modifier, renderContent)
    }

    /** 渲染 Tab 页面。 */
    @Composable
    fun TabBlock(
        node: TabBlock,
        theme: MarkdownTheme,
        modifier: Modifier,
    ) {
        DefaultExtensionProvider.Default.TabBlock(node, theme, modifier)
    }

    // ─────────── 行内扩展 ───────────

    /**
     * 为行内数学公式提供一个 InlineContent 槽位。
     *
     * 返回 `null` 时 renderer 会使用回退显示（原始 LaTeX 文本）。
     */
    fun inlineMathSlot(
        node: InlineMath,
        style: MathStyle,
        context: InlineExtensionContext,
    ): InlineExtensionSlot? = null

    /**
     * 为行内图片提供 InlineContent 槽位。
     *
     * 段落中混有图片的情况，renderer 会自动把独立图片拆分为块级调用 [BlockImage]；
     * 该方法只用于真正嵌入在文本中的小图片。
     */
    fun inlineImageSlot(
        node: Image,
        altText: String,
        style: ImageStyle,
        context: InlineExtensionContext,
    ): InlineExtensionSlot? = null

    /** 为行内 shortcode 提供 InlineContent 槽位。 */
    fun inlineShortcodeSlot(
        node: ShortcodeInline,
        theme: MarkdownTheme,
        context: InlineExtensionContext,
    ): InlineExtensionSlot? = null

    /** 没有任何扩展实现的 provider，全部使用默认 fallback。 */
    companion object None : MarkdownExtensionProvider
}

/**
 * 行内扩展的尺寸 + 内容槽位。
 *
 * 由于 Compose 的 `InlineTextContent` 需要显式声明 Placeholder 尺寸，
 * 扩展实现需要提前给出公式 / 图片等内容的精确宽高，
 * renderer 将使用这些尺寸构建 `Placeholder`，并调用 [content] 绘制实际内容。
 */
class InlineExtensionSlot(
    val width: TextUnit,
    val height: TextUnit,
    val verticalAlign: PlaceholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
    val overflowBehavior: InlineExtensionOverflowBehavior = InlineExtensionOverflowBehavior.None,
    val content: @Composable () -> Unit,
)

enum class InlineExtensionOverflowBehavior {
    None,
    HorizontalScroll,
}

class InlineExtensionContext(
    val density: Density,
    val layoutDirection: LayoutDirection,
    val textMeasurer: TextMeasurer,
    val maxInlineContentWidth: TextUnit? = null,
)
