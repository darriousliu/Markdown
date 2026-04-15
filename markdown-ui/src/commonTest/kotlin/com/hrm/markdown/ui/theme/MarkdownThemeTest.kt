package com.hrm.markdown.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
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
            background = Color.Blue,
            titleColor = Color.Green,
            iconText = "fallback",
        )
        val styles = mapOf(
            "TIP" to AdmonitionStyle(
                borderColor = Color.White,
                background = Color.Black,
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
        val defaultBlockQuote = BlockQuoteStyle()
        val defaultTableOfContents = defaultTableOfContentsStyle()
        val defaultColumnsLayout = ColumnsLayoutStyle()
        val defaultBibliography = defaultBibliographyStyle()
        val defaultDiagram = DiagramStyle()
        val defaultPageBreak = defaultPageBreakStyle()
        val defaultList = ListStyle()
        val defaultTable = TableStyle()
        val defaultFootnote = FootnoteStyle()
        val defaultInlineCode = defaultInlineCodeStyle()
        val defaultKbd = defaultKbdStyle()
        val defaultAdmonition = AdmonitionStyleSet()
        val defaultTaskList = TaskListStyle()

        assertEquals(Color.Transparent, theme.blockQuote.background)
        assertEquals(defaultBlockQuote.contentPadding.toPaddingSnapshot(), theme.blockQuote.contentPadding.toPaddingSnapshot())
        assertNull(theme.blockQuote.textStyle)

        assertEquals(defaultBlockQuote.borderContentSpacing.value, theme.blockQuote.borderContentSpacing.value)

        assertEquals(defaultTableOfContents.section.containerPadding.toPaddingSnapshot(), theme.tableOfContents.section.containerPadding.toPaddingSnapshot())
        assertEquals(defaultTableOfContents.section.itemSpacing.value, theme.tableOfContents.section.itemSpacing.value)
        assertEquals(defaultTableOfContents.section.titleBottomPadding.value, theme.tableOfContents.section.titleBottomPadding.value)
        assertEquals(defaultTableOfContents.indentUnit.value, theme.tableOfContents.indentUnit.value)

        assertEquals(defaultColumnsLayout.columnSpacing.value, theme.columnsLayout.columnSpacing.value)

        assertEquals(defaultBibliography.cornerRadius.value, theme.bibliography.cornerRadius.value)
        assertEquals(defaultBibliography.background, theme.bibliography.background)
        assertEquals(defaultBibliography.section.containerPadding.toPaddingSnapshot(), theme.bibliography.section.containerPadding.toPaddingSnapshot())
        assertEquals(defaultBibliography.section.titleBottomPadding.value, theme.bibliography.section.titleBottomPadding.value)
        assertEquals(defaultBibliography.itemPadding.toPaddingSnapshot(), theme.bibliography.itemPadding.toPaddingSnapshot())

        assertEquals(defaultDiagram.fallbackBackground, theme.diagram.fallbackBackground)
        assertEquals(defaultDiagram.fallbackTitleTextStyle, theme.diagram.fallbackTitleTextStyle)
        assertEquals(defaultDiagram.nodeLabelTextStyle, theme.diagram.nodeLabelTextStyle)
        assertEquals(defaultDiagram.edgeLabelTextStyle, theme.diagram.edgeLabelTextStyle)
        assertEquals(defaultDiagram.actorLabelTextStyle, theme.diagram.actorLabelTextStyle)
        assertEquals(defaultDiagram.canvasInset.value, theme.diagram.canvasInset.value)
        assertEquals(defaultDiagram.canvasPadding.value, theme.diagram.canvasPadding.value)

        assertEquals(defaultPageBreak.itemSpacing.value, theme.pageBreak.itemSpacing.value)
        assertEquals(defaultPageBreak.containerPadding.toPaddingSnapshot(), theme.pageBreak.containerPadding.toPaddingSnapshot())
        assertEquals(defaultPageBreak.titleTextStyle, theme.pageBreak.titleTextStyle)
        assertEquals(defaultPageBreak.titleBottomPadding.value, theme.pageBreak.titleBottomPadding.value)

        assertEquals(defaultList.orderedMarkerWidth.value, theme.list.orderedMarkerWidth.value)
        assertNull(theme.list.orderedMarkerTextStyle)

        assertEquals(TextAlign.Start, theme.image.caption.textAlign)
        assertEquals(TextAlign.Center, theme.figure.caption.textAlign)
        assertTrue(theme.figure.caption.italic)
        assertEquals(theme.image.caption.textStyle, theme.image.placeholderTextStyle)

        assertEquals(defaultTable.cellMaxLines, theme.table.cellMaxLines)
        assertEquals(defaultTable.headerMaxLines, theme.table.headerMaxLines)
        assertEquals(defaultTable.cellVerticalAlignment, theme.table.cellVerticalAlignment)
        assertEquals(defaultTable.headerVerticalAlignment, theme.table.headerVerticalAlignment)
        assertNull(theme.table.cellTextAlign)
        assertNull(theme.table.headerTextAlign)

        assertEquals(defaultFootnote.definitionPadding.toPaddingSnapshot(), theme.footnote.definitionPadding.toPaddingSnapshot())
        assertEquals(defaultFootnote.definitionIndent.value, theme.footnote.definitionIndent.value)

        assertEquals(defaultInlineCode.padding.toPaddingSnapshot(), theme.inlineCode.padding.toPaddingSnapshot())
        assertNull(theme.inlineCode.borderColor)
        assertEquals(defaultInlineCode.placeholderVerticalAlign, theme.inlineCode.placeholderVerticalAlign)
        assertEquals(defaultInlineCode.contentVerticalAlignment, theme.inlineCode.contentVerticalAlignment)
        assertEquals(defaultKbd.padding.toPaddingSnapshot(), theme.kbd.padding.toPaddingSnapshot())
        assertNull(theme.kbd.borderColor)
        assertEquals(defaultKbd.placeholderVerticalAlign, theme.kbd.placeholderVerticalAlign)
        assertEquals(defaultKbd.contentVerticalAlignment, theme.kbd.contentVerticalAlignment)
        assertEquals(defaultAdmonition.padding.toPaddingSnapshot(), theme.admonition.padding.toPaddingSnapshot())
        assertTrue(theme.admonition.cornerRadius.value >= 0f)
        assertNull(theme.admonition.contentTextStyle)

        assertEquals(defaultTaskList.checkedBackgroundColor, theme.taskList.checkedBackgroundColor)
        assertEquals(defaultTaskList.checkedBorderColor, theme.taskList.checkedBorderColor)
        assertEquals(defaultTaskList.uncheckedBackgroundColor, theme.taskList.uncheckedBackgroundColor)
        assertEquals(defaultTaskList.uncheckedBorderColor, theme.taskList.uncheckedBorderColor)
        assertEquals(defaultTaskList.cornerRadius.value, theme.taskList.cornerRadius.value)
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
