package com.hrm.markdown.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
        assertNull(theme.blockQuote.textStyle)

        assertEquals(TextAlign.Start, theme.image.captionTextAlign)
        assertEquals(TextAlign.Center, theme.figure.captionTextAlign)
        assertTrue(theme.figure.captionItalic)

        assertNull(theme.inlineCode.borderColor)
        assertNull(theme.kbd.borderColor)
        assertTrue(theme.admonition.cornerRadius.value >= 0f)
        assertNull(theme.admonition.contentTextStyle)
    }
}
