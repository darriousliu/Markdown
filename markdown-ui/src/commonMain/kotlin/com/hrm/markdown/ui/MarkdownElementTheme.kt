package com.hrm.markdown.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 按 Markdown 元素分组的主题定义，避免将所有样式平铺在一个类中。
 */
@Immutable
data class MarkdownElementTheme(
    val heading: HeadingTheme = HeadingTheme(),
    val paragraph: ParagraphTheme = ParagraphTheme(),
    val blockQuote: BlockQuoteTheme = BlockQuoteTheme(),
    val codeBlock: CodeBlockTheme = CodeBlockTheme(),
    val list: ListTheme = ListTheme(),
    val table: TableTheme = TableTheme(),
    val math: MathTheme = MathTheme(),
    val modifiers: MarkdownElementModifiers = MarkdownElementModifiers(),
)

@Immutable data class HeadingTheme(val styles: List<TextStyle> = emptyList())
@Immutable data class ParagraphTheme(val textStyle: TextStyle = TextStyle(fontSize = 16.sp, lineHeight = 24.sp))
@Immutable data class BlockQuoteTheme(val borderColor: Color = Color(0xFFD0D7DE), val borderWidth: Dp = 4.dp, val padding: Dp = 12.dp)
@Immutable data class CodeBlockTheme(val textStyle: TextStyle = TextStyle(fontSize = 14.sp), val padding: Dp = 12.dp)
@Immutable data class ListTheme(val indent: Dp = 24.dp, val bulletColor: Color = Color(0xFF1F2328))
@Immutable data class TableTheme(val borderColor: Color = Color(0xFFD0D7DE), val cellPadding: Dp = 8.dp)
@Immutable data class MathTheme(val fontSize: Float = 16f, val color: Color = Color(0xFF1F2328))

/**
 * 每个元素都可以注入外部 Modifier，内部不强加业务布局。
 */
@Immutable
data class MarkdownElementModifiers(
    val document: Modifier = Modifier,
    val heading: Modifier = Modifier,
    val paragraph: Modifier = Modifier,
    val blockQuote: Modifier = Modifier,
    val codeBlock: Modifier = Modifier,
    val list: Modifier = Modifier,
    val table: Modifier = Modifier,
    val math: Modifier = Modifier,
    val customContainer: Modifier = Modifier,
    val shortcode: Modifier = Modifier,
    val diagram: Modifier = Modifier,
)
