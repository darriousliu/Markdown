package com.hrm.markdown.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlaceholderVerticalAlign
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
    /** 外部为不同 Markdown 元素注入的容器 Modifier。 */
    val modifiers: MarkdownElementModifiers = MarkdownElementModifiers.None,
    /** 文档级通用样式，例如顶层 block 间距。 */
    val document: DocumentStyle = DocumentStyle(),
    /** 目录块样式。 */
    val tableOfContents: TableOfContentsStyle = defaultTableOfContentsStyle(),
    /** 多列布局块样式。 */
    val columnsLayout: ColumnsLayoutStyle = ColumnsLayoutStyle(),
    /** 参考文献块样式。 */
    val bibliography: BibliographyStyle = defaultBibliographyStyle(),
    /** 图表与图表降级渲染样式。 */
    val diagram: DiagramStyle = DiagramStyle(),
    /** 分页符块样式。 */
    val pageBreak: PageBreakStyle = defaultPageBreakStyle(),
    /** 标题样式。 */
    val heading: HeadingStyle = HeadingStyle(),
    /** 段落样式。 */
    val paragraph: ParagraphStyle = ParagraphStyle(),
    /** 引用块样式。 */
    val blockQuote: BlockQuoteStyle = BlockQuoteStyle(),
    /** 列表样式。 */
    val list: ListStyle = ListStyle(),
    /** 代码块样式。 */
    val codeBlock: CodeBlockStyle = CodeBlockStyle(),
    /** 行内代码样式。 */
    val inlineCode: InlineCodeStyle = defaultInlineCodeStyle(),
    /** 表格样式。 */
    val table: TableStyle = TableStyle(),
    /** 分割线样式。 */
    val thematicBreak: ThematicBreakStyle = ThematicBreakStyle(),
    /** 链接样式。 */
    val link: LinkStyle = defaultLinkStyle(),
    /** 斜体强调样式。 */
    val emphasis: EmphasisStyle = defaultEmphasisStyle(),
    /** 粗体强调样式。 */
    val strongEmphasis: StrongEmphasisStyle = defaultStrongEmphasisStyle(),
    /** 删除线样式。 */
    val strikethrough: StrikethroughStyle = defaultStrikethroughStyle(),
    /** 高亮文本样式。 */
    val highlight: HighlightStyle = defaultHighlightStyle(),
    /** 上标样式。 */
    val superscript: SuperscriptStyle = defaultSuperscriptStyle(),
    /** 下标样式。 */
    val subscript: SubscriptStyle = defaultSubscriptStyle(),
    /** 插入文本样式。 */
    val insertedText: InsertedTextStyle = defaultInsertedTextStyle(),
    /** HTML block 样式。 */
    val htmlBlock: HtmlBlockStyle = HtmlBlockStyle(),
    /** 行内 HTML 样式。 */
    val inlineHtml: InlineHtmlStyle = defaultInlineHtmlStyle(),
    /** 数学公式块样式。 */
    val math: MathStyle = MathStyle(),
    /** 图片本体样式。 */
    val image: ImageStyle = ImageStyle(),
    /** 脚注样式。 */
    val footnote: FootnoteStyle = FootnoteStyle(),
    /** 键盘按键样式。 */
    val kbd: KbdStyle = defaultKbdStyle(),
    /** 缩写样式。 */
    val abbreviation: AbbreviationStyle = defaultAbbreviationStyle(),
    /** Admonition 样式集合。 */
    val admonition: AdmonitionStyleSet = AdmonitionStyleSet(),
    /** 剧透文本样式。 */
    val spoiler: SpoilerStyle = SpoilerStyle(),
    /** 任务列表样式。 */
    val taskList: TaskListStyle = TaskListStyle(),
    /** 定义列表样式。 */
    val definitionList: DefinitionListStyle = DefinitionListStyle(),
    /** Figure 媒体块样式。 */
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

/**
 * 供多个块级 fallback 区域复用的共享区域样式。
 *
 * 仅在不同元素之间存在明显重复结构时抽取。
 */
@Immutable
data class BlockSectionStyle(
    /** 整个块级区域的内边距。 */
    val containerPadding: PaddingValues = PaddingValues(0.dp),
    /** 块内部相邻条目之间的垂直间距。 */
    val itemSpacing: Dp = 2.dp,
    /** 标题文本样式；为 null 时由具体 renderer 决定回退样式。 */
    val titleTextStyle: TextStyle? = null,
    /** 标题与后续内容之间的垂直间距。 */
    val titleBottomPadding: Dp = 4.dp,
)

@Immutable
data class TableOfContentsStyle(
    /** 目录块共享的区域与标题样式。 */
    val section: BlockSectionStyle = BlockSectionStyle(
        containerPadding = PaddingValues(vertical = 4.dp),
        itemSpacing = 2.dp,
        titleBottomPadding = 4.dp,
    ),
    /** 不同目录层级之间的额外缩进步长。 */
    val indentUnit: Dp = 12.dp,
)

@Immutable
data class ColumnsLayoutStyle(
    /** 多列布局中列与列之间的间距。 */
    val columnSpacing: Dp = 8.dp,
)

@Immutable
data class BibliographyStyle(
    /** 参考文献块共享的区域与标题样式。 */
    val section: BlockSectionStyle = BlockSectionStyle(
        containerPadding = PaddingValues(12.dp),
        itemSpacing = 0.dp,
        titleBottomPadding = 8.dp,
    ),
    /** 参考文献整体卡片背景色。 */
    val background: Color = Color(0xFFF6F8FA),
    /** 参考文献整体卡片圆角。 */
    val cornerRadius: Dp = 8.dp,
    /** 单条参考文献项的内边距。 */
    val itemPadding: PaddingValues = PaddingValues(vertical = 2.dp),
)

@Immutable
data class DiagramStyle(
    /** 无法解析为具体图表时的降级容器背景色。 */
    val fallbackBackground: Color = Color(0xFFF0F4F8),
    /** 降级图表标题文本样式。 */
    val fallbackTitleTextStyle: TextStyle = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF57606A),
    ),
    /** 降级图表标题与内容之间的间距。 */
    val fallbackTitleBottomPadding: Dp = 8.dp,
    /** 降级图表标题图标与文字之间的间距。 */
    val fallbackIconEndPadding: Dp = 6.dp,
    /** 降级图表中不同说明段之间的间距。 */
    val fallbackSpacerHeight: Dp = 4.dp,
    /** 节点标签文本样式。 */
    val nodeLabelTextStyle: TextStyle = TextStyle(fontSize = 13.sp),
    /** 连线标签文本样式。 */
    val edgeLabelTextStyle: TextStyle = TextStyle(fontSize = 11.sp),
    /** 时序图参与者标签文本样式。 */
    val actorLabelTextStyle: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
    /** 图表画布内容距外框的额外 inset。 */
    val canvasInset: Dp = 8.dp,
    /** 图表画布自身的内边距。 */
    val canvasPadding: Dp = 4.dp,
)

typealias PageBreakStyle = BlockSectionStyle

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
    /** 段落文本样式。 */
    val textStyle: TextStyle = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
)

@Immutable
data class BlockQuoteStyle(
    /** 引用块左侧边线颜色。 */
    val borderColor: Color = Color(0xFFD0D7DE),
    /** 引用块左侧边线宽度。 */
    val borderWidth: Dp = 4.dp,
    /** 引用块边线与内容区之间的额外间距。 */
    val borderContentSpacing: Dp = 0.dp,
    /** 引用块内容区内边距。 */
    val contentPadding: PaddingValues = PaddingValues(12.dp),
    /** 引用块默认文本颜色。 */
    val color: Color = Color(0xFF656D76),
    /** 引用块背景色。 */
    val background: Color = Color.Transparent,
    /** 引用块整体圆角。 */
    val cornerRadius: Dp = 0.dp,
    /** 引用块内部文本样式；为 null 时沿用局部段落文本样式。 */
    val textStyle: TextStyle? = null,
)

@Immutable
data class ListStyle(
    /** 列表项整体缩进。 */
    val indent: Dp = 24.dp,
    /** 无序列表 marker 预留宽度。 */
    val markerWidth: Dp = 24.dp,
    /** 有序列表 marker 预留宽度。 */
    val orderedMarkerWidth: Dp = 24.dp,
    /** 无序列表 bullet 颜色。 */
    val bulletColor: Color = Color(0xFF1F2328),
    /** 有序列表 marker 文本样式；为 null 时沿用段落样式。 */
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
    /** 代码块正文文本样式。 */
    val textStyle: TextStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    /** 代码块背景色。 */
    val background: Color = Color(0xFFF6F8FA),
    /** 代码块整体圆角。 */
    val cornerRadius: Dp = 8.dp,
    /** 代码块正文区域内边距。 */
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

/**
 * 行内 chip 类型元素的共享样式。
 *
 * 适用于 `inlineCode`、`kbd` 这类“文本 + 背景容器”的行内节点。
 */
@Immutable
data class InlineChipStyle(
    /** 行内 chip 文本样式。 */
    val textStyle: SpanStyle = SpanStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
    ),
    /** 行内 chip 背景色。 */
    val background: Color = Color(0xFFEFF1F3),
    /** 行内 chip 圆角。 */
    val cornerRadius: Dp = 4.dp,
    /** 行内 chip 内边距。 */
    val padding: PaddingValues = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
    /** 行内 chip 边框颜色；为 null 时不绘制边框。 */
    val borderColor: Color? = null,
    /** 行内 chip 边框宽度。 */
    val borderWidth: Dp = 0.dp,
    /** AnnotatedString 占位符在行内文本中的垂直对齐方式。 */
    val placeholderVerticalAlign: PlaceholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
    /** chip 内部内容在自身容器中的垂直对齐方式。 */
    val contentVerticalAlignment: InlineChipContentVerticalAlignment = InlineChipContentVerticalAlignment.Center,
)

typealias InlineCodeStyle = InlineChipStyle

@Immutable
data class TableStyle(
    /** 表格边框颜色。 */
    val borderColor: Color = Color(0xFFD0D7DE),
    /** 表格外边框宽度。 */
    val borderWidth: Dp = 1.dp,
    /** 单元格边框宽度。 */
    val cellBorderWidth: Dp = 0.5.dp,
    /** 表头背景色。 */
    val headerBackground: Color = Color(0xFFF6F8FA),
    /** 单元格内边距。 */
    val cellPadding: PaddingValues = PaddingValues(8.dp),
    /** 普通单元格文本样式。 */
    val cellTextStyle: TextStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    /** 表头单元格文本样式。 */
    val headerTextStyle: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    /** 普通单元格最大显示行数。 */
    val cellMaxLines: Int = 1,
    /** 表头单元格最大显示行数。 */
    val headerMaxLines: Int = 1,
    /** 普通单元格内容垂直对齐。 */
    val cellVerticalAlignment: TableCellVerticalAlignment = TableCellVerticalAlignment.Center,
    /** 表头单元格内容垂直对齐。 */
    val headerVerticalAlignment: TableCellVerticalAlignment = TableCellVerticalAlignment.Center,
    /** 普通单元格文本对齐；为 null 时优先采用 markdown 对齐信息。 */
    val cellTextAlign: TextAlign? = null,
    /** 表头单元格文本对齐；为 null 时优先采用 markdown 对齐信息。 */
    val headerTextAlign: TextAlign? = null,
)

@Immutable
data class ThematicBreakStyle(
    /** 分割线颜色。 */
    val color: Color = Color(0xFFD0D7DE),
    /** 分割线厚度。 */
    val thickness: Dp = 1.dp,
)

/** 多种行内 span 样式共享的最小样式单元。 */
@Immutable
data class InlineSpanTextStyle(
    /** 行内 span 文本样式。 */
    val textStyle: SpanStyle = SpanStyle(),
)

typealias LinkStyle = InlineSpanTextStyle
typealias EmphasisStyle = InlineSpanTextStyle
typealias StrongEmphasisStyle = InlineSpanTextStyle
typealias StrikethroughStyle = InlineSpanTextStyle
typealias HighlightStyle = InlineSpanTextStyle
typealias SuperscriptStyle = InlineSpanTextStyle
typealias SubscriptStyle = InlineSpanTextStyle
typealias InsertedTextStyle = InlineSpanTextStyle

@Immutable
data class HtmlBlockStyle(
    /** HTML block 文本样式。 */
    val textStyle: TextStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    /** HTML block 背景色。 */
    val background: Color = Color.Transparent,
    /** HTML block 圆角。 */
    val cornerRadius: Dp = 0.dp,
    /** HTML block 内边距。 */
    val padding: PaddingValues = PaddingValues(0.dp),
)

typealias InlineHtmlStyle = InlineSpanTextStyle

@Immutable
data class MathStyle(
    /** 数学块字号。 */
    val fontSize: TextUnit = 16.sp,
    /** 数学块文字颜色。 */
    val color: Color = Color(0xFF1F2328),
    /** 数学块背景色。 */
    val background: Color = Color(0xFFF6F8FA),
    /** 数学块内边距。 */
    val padding: PaddingValues = PaddingValues(12.dp),
    /** 数学块圆角。 */
    val cornerRadius: Dp = 8.dp,
)

/** 图片与 figure 等媒体元素共享的容器样式。 */
@Immutable
data class MediaContainerStyle(
    /** 媒体容器背景色。 */
    val background: Color = Color.Transparent,
    /** 媒体容器边框颜色。 */
    val borderColor: Color = Color.Transparent,
    /** 媒体容器边框宽度。 */
    val borderWidth: Dp = 0.dp,
    /** 媒体容器圆角。 */
    val cornerRadius: Dp = 0.dp,
    /** 媒体容器内边距。 */
    val padding: PaddingValues = PaddingValues(0.dp),
)

/** 图片与 figure 等媒体元素共享的 caption 样式。 */
@Immutable
data class MediaCaptionStyle(
    /** 媒体说明文字样式。 */
    val textStyle: TextStyle = TextStyle(
        fontSize = 13.sp,
        color = Color(0xFF656D76),
    ),
    /** 媒体内容与 caption 之间的垂直间距。 */
    val topPadding: Dp = 4.dp,
    /** caption 文本对齐。 */
    val textAlign: TextAlign = TextAlign.Start,
    /** 是否以斜体显示 caption。 */
    val italic: Boolean = false,
)

@Immutable
data class ImageStyle(
    /** 占位符默认尺寸，供扩展 provider 参考。 */
    val defaultWidth: Dp = 200.dp,
    /** 占位符默认高度，供扩展 provider 参考。 */
    val defaultHeight: Dp = 150.dp,
    /** 图片外框样式。 */
    val frame: MediaContainerStyle = MediaContainerStyle(
        padding = PaddingValues(vertical = 4.dp),
    ),
    /** 图片内容区内边距。 */
    val contentPadding: PaddingValues = PaddingValues(12.dp),
    /** 图片说明文本样式，供占位文案或外部扩展渲染的 caption 复用。 */
    val caption: MediaCaptionStyle = MediaCaptionStyle(),
    /** 图片占位文本样式。 */
    val placeholderTextStyle: TextStyle = caption.textStyle,
)

@Immutable
data class FootnoteStyle(
    /** 行内脚注引用样式。 */
    val textStyle: SpanStyle = SpanStyle(fontSize = 12.sp),
    /** 脚注定义编号或标签的文本样式。 */
    val definitionLabelTextStyle: TextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    /** 脚注定义块内边距。 */
    val definitionPadding: PaddingValues = PaddingValues(top = 4.dp),
    /** 脚注定义内容相对标签的缩进。 */
    val definitionIndent: Dp = 16.dp,
)

typealias KbdStyle = InlineChipStyle
typealias AbbreviationStyle = InlineSpanTextStyle

/**
 * Admonition 风格映射。
 *
 * 使用 [styles] map 自定义 "NOTE"、"TIP" 等类型，未匹配时回退到 [fallback]。
 */
@Immutable
data class AdmonitionStyleSet(
    /** 按 admonition 类型映射的样式表。 */
    val styles: Map<String, AdmonitionStyle> = defaultAdmonitionStyles(),
    /** 未命中类型时使用的回退样式。 */
    val fallback: AdmonitionStyle = AdmonitionStyle(
        borderColor = Color(0xFFD0D7DE),
        background = Color(0xFFF6F8FA),
        titleColor = Color(0xFF1F2328),
        iconText = "ℹ️",
    ),
    /** Admonition 内容区内边距。 */
    val padding: PaddingValues = PaddingValues(12.dp),
    /** Admonition 左侧强调边线宽度。 */
    val borderWidth: Dp = 4.dp,
    /** Admonition 标题文本样式。 */
    val titleTextStyle: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
    /** Admonition 外框圆角。 */
    val cornerRadius: Dp = 8.dp,
    /** 标题区与正文之间的垂直间距。 */
    val titleContentSpacing: Dp = 8.dp,
    /** 图标与标题文字之间的间距。 */
    val iconSpacing: Dp = 8.dp,
    /** 正文文本样式；为 null 时沿用局部段落样式。 */
    val contentTextStyle: TextStyle? = null,
) {
    fun resolve(type: String): AdmonitionStyle =
        styles[type] ?: styles[type.uppercase()] ?: fallback
}

@Immutable
data class AdmonitionStyle(
    /** Admonition 左侧边线颜色。 */
    val borderColor: Color,
    /** Admonition 背景色。 */
    val background: Color,
    /** Admonition 标题颜色。 */
    val titleColor: Color,
    /** Admonition 标题图标文本。 */
    val iconText: String,
)

@Immutable
data class SpoilerStyle(
    /** 剧透遮罩背景色。 */
    val background: Color = Color(0xFF3A3A3A),
)

@Immutable
data class TaskListStyle(
    /** 勾选状态前景色。 */
    val checkedColor: Color = Color(0xFF1A7F37),
    /** 未勾选状态前景色。 */
    val uncheckedColor: Color = Color(0xFFD0D7DE),
    /** 已勾选复选框背景色。 */
    val checkedBackgroundColor: Color = checkedColor,
    /** 已勾选复选框边框色。 */
    val checkedBorderColor: Color = checkedColor,
    /** 未勾选复选框背景色。 */
    val uncheckedBackgroundColor: Color = Color.Transparent,
    /** 未勾选复选框边框色。 */
    val uncheckedBorderColor: Color = uncheckedColor,
    /** 复选框尺寸。 */
    val boxSize: Dp = 18.dp,
    /** 复选框圆角。 */
    val cornerRadius: Dp = 4.dp,
    /** 复选框边框或勾线宽度。 */
    val strokeWidth: Dp = 1.5.dp,
    /** 勾号颜色。 */
    val checkmarkColor: Color = Color.White,
    /** 勾号文本内容。 */
    val checkmarkText: String = "✓",
    /** 勾号文本样式；为 null 时由 renderer 根据尺寸推导。 */
    val checkmarkTextStyle: TextStyle? = null,
)

@Immutable
data class DefinitionListStyle(
    /** 术语文本样式。 */
    val termTextStyle: TextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    /** 说明内容相对术语的缩进。 */
    val descriptionIndent: Dp = 24.dp,
    /** 相邻定义项之间的间距。 */
    val itemSpacing: Dp = 4.dp,
)

@Immutable
data class FigureStyle(
    /** Figure 整体容器样式。 */
    val container: MediaContainerStyle = MediaContainerStyle(),
    /** Figure 内媒体内容的水平对齐方式。 */
    val contentAlignment: FigureContentAlignment = FigureContentAlignment.Center,
    /** Figure caption 样式。 */
    val caption: MediaCaptionStyle = MediaCaptionStyle(
        textAlign = TextAlign.Center,
        italic = true,
    ),
)

/** 表格单元格的垂直对齐选项。 */
@Immutable
enum class TableCellVerticalAlignment {
    Top,
    Center,
    Bottom,
}

/** Figure 内主媒体内容的水平对齐选项。 */
@Immutable
enum class FigureContentAlignment {
    Start,
    Center,
    End,
}

/** 行内 chip 占位内容在自身容器中的垂直对齐选项。 */
@Immutable
enum class InlineChipContentVerticalAlignment {
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

internal fun defaultInlineCodeStyle(): InlineCodeStyle = InlineChipStyle(
    textStyle = SpanStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
    ),
    background = Color(0xFFEFF1F3),
    cornerRadius = 4.dp,
    padding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
)

internal fun defaultKbdStyle(): KbdStyle = InlineChipStyle(
    textStyle = SpanStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
    ),
    background = Color(0xFFEFF1F3),
    cornerRadius = 4.dp,
    padding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
)

private fun defaultLinkStyle(): LinkStyle = InlineSpanTextStyle(
    textStyle = SpanStyle(
        color = Color(0xFF0969DA),
        textDecoration = TextDecoration.Underline,
    ),
)

private fun defaultEmphasisStyle(): EmphasisStyle =
    InlineSpanTextStyle(textStyle = SpanStyle(fontStyle = FontStyle.Italic))

private fun defaultStrongEmphasisStyle(): StrongEmphasisStyle =
    InlineSpanTextStyle(textStyle = SpanStyle(fontWeight = FontWeight.Bold))

private fun defaultStrikethroughStyle(): StrikethroughStyle =
    InlineSpanTextStyle(textStyle = SpanStyle(textDecoration = TextDecoration.LineThrough))

private fun defaultHighlightStyle(): HighlightStyle =
    InlineSpanTextStyle(textStyle = SpanStyle(background = Color(0xFFFFF3B0)))

private fun defaultSuperscriptStyle(): SuperscriptStyle =
    InlineSpanTextStyle(textStyle = SpanStyle(fontSize = 12.sp))

private fun defaultSubscriptStyle(): SubscriptStyle =
    InlineSpanTextStyle(textStyle = SpanStyle(fontSize = 12.sp))

private fun defaultInsertedTextStyle(): InsertedTextStyle =
    InlineSpanTextStyle(textStyle = SpanStyle(textDecoration = TextDecoration.Underline))

private fun defaultInlineHtmlStyle(): InlineHtmlStyle = InlineSpanTextStyle(
    textStyle = SpanStyle(
        color = Color.Gray,
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
    ),
)

private fun defaultAbbreviationStyle(): AbbreviationStyle =
    InlineSpanTextStyle(textStyle = SpanStyle(textDecoration = TextDecoration.Underline))

internal fun defaultTableOfContentsStyle(): TableOfContentsStyle = TableOfContentsStyle()

internal fun defaultBibliographyStyle(): BibliographyStyle = BibliographyStyle()

internal fun defaultPageBreakStyle(): PageBreakStyle = BlockSectionStyle(
    itemSpacing = 8.dp,
    titleTextStyle = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
    ),
    titleBottomPadding = 0.dp,
)

private fun defaultAdmonitionStyles(): Map<String, AdmonitionStyle> = mapOf(
    "NOTE" to AdmonitionStyle(
        borderColor = Color(0xFF0969DA),
        background = Color(0xFFDDF4FF),
        iconText = "ℹ️",
        titleColor = Color(0xFF0969DA),
    ),
    "TIP" to AdmonitionStyle(
        borderColor = Color(0xFF1A7F37),
        background = Color(0xFFDCFFE4),
        iconText = "💡",
        titleColor = Color(0xFF1A7F37),
    ),
    "IMPORTANT" to AdmonitionStyle(
        borderColor = Color(0xFF8250DF),
        background = Color(0xFFFBEFFF),
        iconText = "❗",
        titleColor = Color(0xFF8250DF),
    ),
    "WARNING" to AdmonitionStyle(
        borderColor = Color(0xFFBF8700),
        background = Color(0xFFFFF8C5),
        iconText = "⚠️",
        titleColor = Color(0xFFBF8700),
    ),
    "CAUTION" to AdmonitionStyle(
        borderColor = Color(0xFFCF222E),
        background = Color(0xFFFFEBE9),
        iconText = "🔴",
        titleColor = Color(0xFFCF222E),
    ),
)
