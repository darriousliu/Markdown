package com.hrm.markdown.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MarkdownThemeTest {

    @Test
    fun headingStyle_exposesSixLevels() {
        val theme = MarkdownTheme()

        assertEquals(6, theme.heading.textStyles.size)
        assertTrue(theme.heading.textStyles.distinct().size > 1)
    }

    @Test
    fun admonition_resolveFallsBackWhenTypeMissing() {
        val fallback = AdmonitionStyle(
            borderColor = Color.Red,
            backgroundColor = Color.Blue,
            titleColor = Color.Green,
            iconText = "fallback",
        )
        val styles = mapOf(
            "TIP" to AdmonitionStyle(
                borderColor = Color.White,
                backgroundColor = Color.Black,
                titleColor = Color.Gray,
                iconText = "tip",
            )
        )

        val styleSet = AdmonitionStyleSet(styles = styles, fallback = fallback)

        assertEquals(styles["TIP"], styleSet.resolve("TIP"))
        assertEquals(styles["TIP"], styleSet.resolve("tip"))
        assertEquals(fallback, styleSet.resolve("unknown"))
    }

    @Test
    fun newStyleFields_haveBackwardCompatibleDefaults() {
        val theme = MarkdownTheme()

        assertEquals(Color.Transparent, theme.blockQuote.backgroundColor)
        assertEquals(PaddingValues(12.dp).toPaddingSnapshot(), theme.blockQuote.contentPadding.toPaddingSnapshot())
        assertNull(theme.blockQuote.textStyle)

        assertEquals(24.dp.value, theme.list.orderedMarkerWidth.value)
        assertNull(theme.list.orderedMarkerTextStyle)

        assertEquals(TextAlign.Start, theme.image.captionTextAlign)
        assertEquals(TextAlign.Center, theme.figure.captionTextAlign)
        assertTrue(theme.figure.captionItalic)

        assertEquals(1, theme.table.cellMaxLines)
        assertEquals(1, theme.table.headerMaxLines)
        assertEquals(TableCellVerticalAlignment.Center, theme.table.cellVerticalAlignment)
        assertEquals(TableCellVerticalAlignment.Center, theme.table.headerVerticalAlignment)

        assertEquals(PaddingValues(top = 4.dp).toPaddingSnapshot(), theme.footnote.definitionPadding.toPaddingSnapshot())
        assertEquals(16.dp.value, theme.footnote.definitionIndent.value)

        assertEquals(PaddingValues(horizontal = 4.dp, vertical = 2.dp).toPaddingSnapshot(), theme.inlineCode.padding.toPaddingSnapshot())
        assertNull(theme.inlineCode.borderColor)
        assertEquals(PaddingValues(horizontal = 6.dp, vertical = 2.dp).toPaddingSnapshot(), theme.kbd.padding.toPaddingSnapshot())
        assertNull(theme.kbd.borderColor)
        assertEquals(PaddingValues(12.dp).toPaddingSnapshot(), theme.admonition.padding.toPaddingSnapshot())
        assertTrue(theme.admonition.cornerRadius.value >= 0f)
        assertNull(theme.admonition.contentTextStyle)

        assertEquals(Color.Transparent, theme.taskList.uncheckedBackgroundColor)
        assertEquals(4.dp.value, theme.taskList.cornerRadius.value)
        assertEquals("✓", theme.taskList.checkmarkText)
        assertNull(theme.taskList.checkmarkTextStyle)
    }
}

private fun PaddingValues.toPaddingSnapshot(): List<Float> = listOf(
    calculateLeftPadding(LayoutDirection.Ltr).value,
    calculateTopPadding().value,
    calculateRightPadding(LayoutDirection.Ltr).value,
    calculateBottomPadding().value,
)
