package com.hrm.markdown.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

/**
 * 默认 [MarkdownTheme] 工厂。
 */
object MarkdownThemeDefaults {
    fun light(): MarkdownTheme = MarkdownTheme()

    fun dark(): MarkdownTheme {
        val onSurface = Color(0xFFE6EDF3)
        return MarkdownTheme(
            heading = HeadingStyle(
                textStyles = darkHeadingTextStyles(onSurface),
                underlineColor = Color(0xFF3D444D),
            ),
            paragraph = ParagraphStyle(
                textStyle = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, color = onSurface),
            ),
            blockQuote = BlockQuoteStyle(
                borderColor = Color(0xFF3D444D),
                color = Color(0xFF9198A1),
            ),
            bibliography = BibliographyStyle(
                background = Color(0xFF161B22),
            ),
            codeBlock = CodeBlockStyle(
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = onSurface,
                ),
                background = Color(0xFF161B22),
                titleBackground = Color(0xFF21262D),
                lineNumberColor = Color(0xFF7D8590),
                lineHighlightBackground = Color(0xFF5C4B00),
            ),
            inlineCode = InlineCodeStyle(
                background = Color(0xFF343942),
            ),
            table = TableStyle(
                borderColor = Color(0xFF3D444D),
                headerBackground = Color(0xFF161B22),
                cellTextStyle = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, color = onSurface),
                headerTextStyle = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurface,
                ),
            ),
            thematicBreak = ThematicBreakStyle(color = Color(0xFF3D444D)),
            link = LinkStyle(
                textStyle = SpanStyle(
                    color = Color(0xFF4493F8),
                    textDecoration = TextDecoration.Underline,
                ),
            ),
            highlight = HighlightStyle(
                textStyle = SpanStyle(background = Color(0xFF5C4B00)),
            ),
            math = MathStyle(color = onSurface, background = Color(0xFF161B22)),
            admonition = AdmonitionStyleSet(styles = darkAdmonitionStyles()),
            kbd = KbdStyle(background = Color(0xFF343942)),
            spoiler = SpoilerStyle(background = Color(0xFF3D444D)),
            taskList = TaskListStyle(
                checkedColor = Color(0xFF3FB950),
                uncheckedColor = Color(0xFF3D444D),
            ),
            list = ListStyle(bulletColor = onSurface),
        )
    }

    /** 跟随系统日夜间模式自动选择主题。 */
    @Composable
    @ReadOnlyComposable
    fun auto(isDark: Boolean = isSystemInDarkTheme()): MarkdownTheme =
        if (isDark) dark() else light()
}

private fun darkHeadingTextStyles(color: Color): List<TextStyle> = listOf(
    TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp, color = color),
    TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp, color = color),
    TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp,
        color = color
    ),
    TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 24.sp,
        color = color
    ),
    TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 20.sp,
        color = color
    ),
    TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp, color = color),
)

private fun darkAdmonitionStyles(): Map<String, AdmonitionStyle> = mapOf(
    "NOTE" to AdmonitionStyle(
        borderColor = Color(0xFF4493F8),
        background = Color(0xFF0D1D30),
        iconText = "ℹ️",
        titleColor = Color(0xFF4493F8),
    ),
    "TIP" to AdmonitionStyle(
        borderColor = Color(0xFF3FB950),
        background = Color(0xFF0D2818),
        iconText = "💡",
        titleColor = Color(0xFF3FB950),
    ),
    "IMPORTANT" to AdmonitionStyle(
        borderColor = Color(0xFFAB7DF8),
        background = Color(0xFF1B1030),
        iconText = "❗",
        titleColor = Color(0xFFAB7DF8),
    ),
    "WARNING" to AdmonitionStyle(
        borderColor = Color(0xFFD29922),
        background = Color(0xFF2A1F00),
        iconText = "⚠️",
        titleColor = Color(0xFFD29922),
    ),
    "CAUTION" to AdmonitionStyle(
        borderColor = Color(0xFFF85149),
        background = Color(0xFF300C0C),
        iconText = "🔴",
        titleColor = Color(0xFFF85149),
    ),
)
