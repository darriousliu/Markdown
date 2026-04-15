package com.hrm.markdown.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Markdown 主题，按元素分组聚合各元素的视觉样式。
 *
 * 与原 renderer 模块把所有样式平铺到单个类不同，本模块中每个 Markdown 元素
 * 都有自己独立的 *Style（例如 [HeadingStyle]、[CodeBlockStyle]）。
 *
 * - 每个 *Style 只包含 *视觉样式*（颜色、字体、padding、borderWidth 等数值），
 *   不包含 Modifier，也不包含任何布局决策（例如 fillMaxWidth）。
 * - 需要由外部注入的 Modifier 全部放在 [MarkdownElementModifiers] 中，
 *   在 renderer 内部只负责将 [MarkdownElementModifiers] 对应的 Modifier 应用到
 *   该元素的外层容器上。
 */
@Immutable
data class MarkdownTheme(
    val modifiers: MarkdownElementModifiers = MarkdownElementModifiers.None,
    val document: DocumentStyle = DocumentStyle(),
    val heading: HeadingStyle = HeadingStyle(),
    val paragraph: ParagraphStyle = ParagraphStyle(),
    val blockQuote: BlockQuoteStyle = BlockQuoteStyle(),
    val list: ListStyle = ListStyle(),
    val codeBlock: CodeBlockStyle = CodeBlockStyle(),
    val inlineCode: InlineCodeStyle = InlineCodeStyle(),
    val table: TableStyle = TableStyle(),
    val thematicBreak: ThematicBreakStyle = ThematicBreakStyle(),
    val link: LinkStyle = LinkStyle(),
    val emphasis: EmphasisStyle = EmphasisStyle(),
    val strongEmphasis: StrongEmphasisStyle = StrongEmphasisStyle(),
    val strikethrough: StrikethroughStyle = StrikethroughStyle(),
    val highlight: HighlightStyle = HighlightStyle(),
    val superscript: SuperscriptStyle = SuperscriptStyle(),
    val subscript: SubscriptStyle = SubscriptStyle(),
    val insertedText: InsertedTextStyle = InsertedTextStyle(),
    val htmlBlock: HtmlBlockStyle = HtmlBlockStyle(),
    val inlineHtml: InlineHtmlStyle = InlineHtmlStyle(),
    val math: MathStyle = MathStyle(),
    val image: ImageStyle = ImageStyle(),
    val footnote: FootnoteStyle = FootnoteStyle(),
    val kbd: KbdStyle = KbdStyle(),
    val abbreviation: AbbreviationStyle = AbbreviationStyle(),
    val admonition: AdmonitionStyleSet = AdmonitionStyleSet(),
    val spoiler: SpoilerStyle = SpoilerStyle(),
    val taskList: TaskListStyle = TaskListStyle(),
    val definitionList: DefinitionListStyle = DefinitionListStyle(),
    val figure: FigureStyle = FigureStyle(),
) {
    companion object {
        /** 亮色默认主题（GitHub Light 风格）。 */
        val Light: MarkdownTheme = MarkdownThemeDefaults.light()

        /** 暗色默认主题（GitHub Dark 风格）。 */
        val Dark: MarkdownTheme = MarkdownThemeDefaults.dark()
    }
}

@Immutable
data class DocumentStyle(
    /** 顶层 block 之间的垂直间距。 */
    val blockSpacing: Dp = 12.dp,
)

@Immutable
data class HeadingStyle(
    /** h1..h6 的文字样式列表。长度应为 6。 */
    val textStyles: List<TextStyle> = defaultHeadingTextStyles(),
    /** h1/h2 下方自动分隔线的厚度。0.dp 表示不绘制。 */
    val underlineThickness: Dp = 1.dp,
    /** h1/h2 下方自动分隔线的颜色。 */
    val underlineColor: Color = Color(0xFFD0D7DE),
    /** 分隔线与标题文字的垂直间距。 */
    val underlinePadding: Dp = 4.dp,
    /** 最多为前几级标题绘制下划线。默认为 2（h1、h2 有下划线）。 */
    val underlineMaxLevel: Int = 2,
)

@Immutable
data class ParagraphStyle(
    val textStyle: TextStyle = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
)

@Immutable
data class BlockQuoteStyle(
    val borderColor: Color = Color(0xFFD0D7DE),
    val borderWidth: Dp = 4.dp,
    val contentPadding: PaddingValues = PaddingValues(12.dp),
    val textColor: Color = Color(0xFF656D76),
    val backgroundColor: Color = Color.Transparent,
    val cornerRadius: Dp = 0.dp,
    val textStyle: TextStyle? = null,
)

@Immutable
data class ListStyle(
    val indent: Dp = 24.dp,
    val markerWidth: Dp = 24.dp,
    val orderedMarkerWidth: Dp = 24.dp,
    val bulletColor: Color = Color(0xFF1F2328),
    val orderedMarkerTextStyle: TextStyle? = null,
    /** tight list 的块间距（列表项之间）。 */
    val tightSpacing: Dp = 2.dp,
    /** loose list 的块间距（空行分隔时）。 */
    val looseSpacing: Dp = 12.dp,
    /** 无序列表的 bullet 文本。 */
    val bullet: String = "•",
)

@Immutable
data class CodeBlockStyle(
    val textStyle: TextStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    val background: Color = Color(0xFFF6F8FA),
    val cornerRadius: Dp = 8.dp,
    val padding: PaddingValues = PaddingValues(12.dp),
    /** 代码块标题栏背景色。 */
    val titleBackground: Color = Color(0xFFEBEDF0),
    /** 代码块标题栏 padding。 */
    val titlePadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    /** 代码块标题栏文字样式。 */
    val titleTextStyle: TextStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
    ),
    /** 行号的颜色。 */
    val lineNumberColor: Color = Color(0xFF6E7681),
    /** 行号区域和正文之间的间距。 */
    val lineNumberPadding: PaddingValues = PaddingValues(end = 12.dp),
    /** 高亮行的背景色。 */
    val lineHighlightBackground: Color = Color(0xFFFFF8C5),
    /** 代码行之间的间距。 */
    val lineSpacing: Dp = 2.dp,
    /** 单行代码容器圆角。 */
    val lineCornerRadius: Dp = 4.dp,
    /** 单行代码容器 padding。 */
    val linePadding: PaddingValues = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
)

@Immutable
data class InlineCodeStyle(
    val textStyle: SpanStyle = SpanStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
    ),
    val background: Color = Color(0xFFEFF1F3),
    val cornerRadius: Dp = 4.dp,
    val padding: PaddingValues = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
    val borderColor: Color? = null,
    val borderWidth: Dp = 0.dp,
)

@Immutable
data class TableStyle(
    val borderColor: Color = Color(0xFFD0D7DE),
    val borderWidth: Dp = 1.dp,
    val cellBorderWidth: Dp = 0.5.dp,
    val headerBackground: Color = Color(0xFFF6F8FA),
    val cellPadding: PaddingValues = PaddingValues(8.dp),
    val cellTextStyle: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    val headerTextStyle: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    val cellMaxLines: Int = 1,
    val headerMaxLines: Int = 1,
    val cellVerticalAlignment: TableCellVerticalAlignment = TableCellVerticalAlignment.Center,
    val headerVerticalAlignment: TableCellVerticalAlignment = TableCellVerticalAlignment.Center,
)

@Immutable
data class ThematicBreakStyle(
    val color: Color = Color(0xFFD0D7DE),
    val thickness: Dp = 1.dp,
)

@Immutable
data class LinkStyle(
    val textStyle: SpanStyle = SpanStyle(
        color = Color(0xFF0969DA),
        textDecoration = TextDecoration.Underline,
    ),
)

@Immutable
data class EmphasisStyle(
    val textStyle: SpanStyle = SpanStyle(fontStyle = FontStyle.Italic),
)

@Immutable
data class StrongEmphasisStyle(
    val textStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.Bold),
)

@Immutable
data class StrikethroughStyle(
    val textStyle: SpanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough),
)

@Immutable
data class HighlightStyle(
    val textStyle: SpanStyle = SpanStyle(background = Color(0xFFFFF3B0)),
)

@Immutable
data class SuperscriptStyle(
    val textStyle: SpanStyle = SpanStyle(fontSize = 12.sp),
)

@Immutable
data class SubscriptStyle(
    val textStyle: SpanStyle = SpanStyle(fontSize = 12.sp),
)

@Immutable
data class InsertedTextStyle(
    val textStyle: SpanStyle = SpanStyle(textDecoration = TextDecoration.Underline),
)

@Immutable
data class HtmlBlockStyle(
    val textStyle: TextStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    val background: Color = Color.Transparent,
    val cornerRadius: Dp = 0.dp,
    val padding: PaddingValues = PaddingValues(0.dp),
)

@Immutable
data class InlineHtmlStyle(
    val textStyle: SpanStyle = SpanStyle(
        color = Color.Gray,
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
    ),
)

@Immutable
data class MathStyle(
    val fontSize: TextUnit = 16.sp,
    val color: Color = Color(0xFF1F2328),
    val background: Color = Color(0xFFF6F8FA),
    val padding: PaddingValues = PaddingValues(12.dp),
    val cornerRadius: Dp = 8.dp,
)

@Immutable
data class ImageStyle(
    /** 占位符默认尺寸，供扩展 provider 参考。 */
    val defaultWidth: Dp = 200.dp,
    val defaultHeight: Dp = 150.dp,
    val background: Color = Color.Transparent,
    val borderColor: Color = Color.Transparent,
    val borderWidth: Dp = 0.dp,
    val cornerRadius: Dp = 0.dp,
    val contentPadding: PaddingValues = PaddingValues(12.dp),
    /** 图片标题（figcaption）样式。 */
    val captionTextStyle: TextStyle = TextStyle(
        fontSize = 13.sp,
        color = Color(0xFF656D76),
    ),
    val captionTopPadding: Dp = 4.dp,
    val captionTextAlign: TextAlign = TextAlign.Start,
)

@Immutable
data class FootnoteStyle(
    val textStyle: SpanStyle = SpanStyle(fontSize = 12.sp),
    val definitionLabelTextStyle: TextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    val definitionPadding: PaddingValues = PaddingValues(top = 4.dp),
    val definitionIndent: Dp = 16.dp,
)

@Immutable
data class KbdStyle(
    val textStyle: SpanStyle = SpanStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
    ),
    val background: Color = Color(0xFFEFF1F3),
    val cornerRadius: Dp = 4.dp,
    val padding: PaddingValues = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
    val borderColor: Color? = null,
    val borderWidth: Dp = 0.dp,
)

@Immutable
data class AbbreviationStyle(
    val textStyle: SpanStyle = SpanStyle(textDecoration = TextDecoration.Underline),
)

/**
 * Admonition 风格映射。
 *
 * 使用 [styles] map 自定义 "NOTE"、"TIP" 等类型，未匹配时回退到 [fallback]。
 */
@Immutable
data class AdmonitionStyleSet(
    val styles: Map<String, AdmonitionStyle> = defaultAdmonitionStyles(),
    val fallback: AdmonitionStyle = AdmonitionStyle(
        borderColor = Color(0xFFD0D7DE),
        backgroundColor = Color(0xFFF6F8FA),
        titleColor = Color(0xFF1F2328),
        iconText = "ℹ️",
    ),
    val padding: PaddingValues = PaddingValues(12.dp),
    val borderWidth: Dp = 4.dp,
    val titleTextStyle: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
    val cornerRadius: Dp = 8.dp,
    val titleContentSpacing: Dp = 8.dp,
    val iconSpacing: Dp = 8.dp,
    val contentTextStyle: TextStyle? = null,
) {
    fun resolve(type: String): AdmonitionStyle =
        styles[type] ?: styles[type.uppercase()] ?: fallback
}

@Immutable
data class AdmonitionStyle(
    val borderColor: Color,
    val backgroundColor: Color,
    val titleColor: Color,
    val iconText: String,
)

@Immutable
data class SpoilerStyle(
    val background: Color = Color(0xFF3A3A3A),
)

@Immutable
data class TaskListStyle(
    val checkedColor: Color = Color(0xFF1A7F37),
    val uncheckedColor: Color = Color(0xFFD0D7DE),
    val uncheckedBackgroundColor: Color = Color.Transparent,
    val boxSize: Dp = 18.dp,
    val cornerRadius: Dp = 4.dp,
    val strokeWidth: Dp = 1.5.dp,
    val checkmarkColor: Color = Color.White,
    val checkmarkText: String = "✓",
    val checkmarkTextStyle: TextStyle? = null,
)

@Immutable
data class DefinitionListStyle(
    val termTextStyle: TextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    val descriptionIndent: Dp = 24.dp,
    val itemSpacing: Dp = 4.dp,
)

@Immutable
data class FigureStyle(
    val captionTextStyle: TextStyle = TextStyle(
        fontSize = 13.sp,
        color = Color(0xFF656D76),
    ),
    val captionTopPadding: Dp = 4.dp,
    val captionTextAlign: TextAlign = TextAlign.Center,
    val captionItalic: Boolean = true,
)

@Immutable
enum class TableCellVerticalAlignment {
    Top,
    Center,
    Bottom,
}

private fun defaultHeadingTextStyles(): List<TextStyle> = listOf(
    TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp),
    TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp),
    TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp),
    TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),
    TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp),
    TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp),
)

private fun defaultAdmonitionStyles(): Map<String, AdmonitionStyle> = mapOf(
    "NOTE" to AdmonitionStyle(
        borderColor = Color(0xFF0969DA),
        backgroundColor = Color(0xFFDDF4FF),
        iconText = "ℹ️",
        titleColor = Color(0xFF0969DA),
    ),
    "TIP" to AdmonitionStyle(
        borderColor = Color(0xFF1A7F37),
        backgroundColor = Color(0xFFDCFFE4),
        iconText = "💡",
        titleColor = Color(0xFF1A7F37),
    ),
    "IMPORTANT" to AdmonitionStyle(
        borderColor = Color(0xFF8250DF),
        backgroundColor = Color(0xFFFBEFFF),
        iconText = "❗",
        titleColor = Color(0xFF8250DF),
    ),
    "WARNING" to AdmonitionStyle(
        borderColor = Color(0xFFBF8700),
        backgroundColor = Color(0xFFFFF8C5),
        iconText = "⚠️",
        titleColor = Color(0xFFBF8700),
    ),
    "CAUTION" to AdmonitionStyle(
        borderColor = Color(0xFFCF222E),
        backgroundColor = Color(0xFFFFEBE9),
        iconText = "🔴",
        titleColor = Color(0xFFCF222E),
    ),
)
