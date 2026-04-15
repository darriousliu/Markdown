package com.hrm.markdown.ui.block

import com.hrm.markdown.ui.MarkdownTheme

internal val MarkdownTheme.headingStyles
    get() = heading.textStyles

internal val MarkdownTheme.bodyStyle
    get() = paragraph.textStyle

internal val MarkdownTheme.codeBlockBackground
    get() = codeBlock.background

internal val MarkdownTheme.codeBlockCornerRadius
    get() = codeBlock.cornerRadius

internal val MarkdownTheme.codeBlockPadding
    get() = codeBlock.padding

internal val MarkdownTheme.codeBlockStyle
    get() = codeBlock.textStyle

internal val MarkdownTheme.codeBlockTitleBackground
    get() = codeBlock.titleBackground

internal val MarkdownTheme.blockQuoteBorderColor
    get() = blockQuote.borderColor

internal val MarkdownTheme.blockQuoteBorderWidth
    get() = blockQuote.borderWidth

internal val MarkdownTheme.blockQuoteBorderContentSpacing
    get() = blockQuote.borderContentSpacing

internal val MarkdownTheme.blockQuotePadding
    get() = blockQuote.contentPadding

internal val MarkdownTheme.blockQuoteTextColor
    get() = blockQuote.color

internal val MarkdownTheme.blockQuoteBackground
    get() = blockQuote.background

internal val MarkdownTheme.blockQuoteCornerRadius
    get() = blockQuote.cornerRadius

internal val MarkdownTheme.blockQuoteTextStyle
    get() = blockQuote.textStyle

internal val MarkdownTheme.dividerColor
    get() = thematicBreak.color

internal val MarkdownTheme.dividerThickness
    get() = thematicBreak.thickness

internal val MarkdownTheme.blockSpacing
    get() = document.blockSpacing

internal val MarkdownTheme.tableOfContentsPadding
    get() = tableOfContents.section.containerPadding

internal val MarkdownTheme.tableOfContentsItemSpacing
    get() = tableOfContents.section.itemSpacing

internal val MarkdownTheme.tableOfContentsTitleTextStyle
    get() = tableOfContents.section.titleTextStyle

internal val MarkdownTheme.tableOfContentsTitleBottomPadding
    get() = tableOfContents.section.titleBottomPadding

internal val MarkdownTheme.tableOfContentsIndentUnit
    get() = tableOfContents.indentUnit

internal val MarkdownTheme.columnsGap
    get() = columnsLayout.columnSpacing

internal val MarkdownTheme.bibliographyCornerRadius
    get() = bibliography.cornerRadius

internal val MarkdownTheme.bibliographyBackground
    get() = bibliography.background

internal val MarkdownTheme.bibliographyPadding
    get() = bibliography.section.containerPadding

internal val MarkdownTheme.bibliographyTitleTextStyle
    get() = bibliography.section.titleTextStyle

internal val MarkdownTheme.bibliographyTitleBottomPadding
    get() = bibliography.section.titleBottomPadding

internal val MarkdownTheme.bibliographyItemSpacing
    get() = bibliography.section.itemSpacing

internal val MarkdownTheme.bibliographyItemPadding
    get() = bibliography.itemPadding

internal val MarkdownTheme.pageBreakSpacing
    get() = pageBreak.itemSpacing

internal val MarkdownTheme.pageBreakPadding
    get() = pageBreak.containerPadding

internal val MarkdownTheme.pageBreakLabelTextStyle
    get() = pageBreak.titleTextStyle

internal val MarkdownTheme.pageBreakLabelBottomPadding
    get() = pageBreak.titleBottomPadding

internal val MarkdownTheme.listIndent
    get() = list.indent

internal val MarkdownTheme.listBulletColor
    get() = list.bulletColor

internal val MarkdownTheme.listMarkerWidth
    get() = list.markerWidth

internal val MarkdownTheme.listOrderedMarkerWidth
    get() = list.orderedMarkerWidth

internal val MarkdownTheme.listOrderedMarkerTextStyle
    get() = list.orderedMarkerTextStyle

internal val MarkdownTheme.tableBorderColor
    get() = table.borderColor

internal val MarkdownTheme.tableBorderWidth
    get() = table.borderWidth

internal val MarkdownTheme.tableCellBorderWidth
    get() = table.cellBorderWidth

internal val MarkdownTheme.tableHeaderBackground
    get() = table.headerBackground

internal val MarkdownTheme.tableCellPadding
    get() = table.cellPadding

internal val MarkdownTheme.tableCellTextStyle
    get() = table.cellTextStyle

internal val MarkdownTheme.tableHeaderTextStyle
    get() = table.headerTextStyle

internal val MarkdownTheme.tableCellMaxLines
    get() = table.cellMaxLines

internal val MarkdownTheme.tableHeaderMaxLines
    get() = table.headerMaxLines

internal val MarkdownTheme.tableCellVerticalAlignment
    get() = table.cellVerticalAlignment

internal val MarkdownTheme.tableHeaderVerticalAlignment
    get() = table.headerVerticalAlignment

internal val MarkdownTheme.tableCellTextAlign
    get() = table.cellTextAlign

internal val MarkdownTheme.tableHeaderTextAlign
    get() = table.headerTextAlign

internal val MarkdownTheme.linkColor
    get() = link.textStyle.color

internal val MarkdownTheme.taskCheckedColor
    get() = taskList.checkedColor

internal val MarkdownTheme.taskUncheckedColor
    get() = taskList.uncheckedColor

internal val MarkdownTheme.taskUncheckedBackgroundColor
    get() = taskList.uncheckedBackgroundColor

internal val MarkdownTheme.taskCheckedBackgroundColor
    get() = taskList.checkedBackgroundColor

internal val MarkdownTheme.taskCheckedBorderColor
    get() = taskList.checkedBorderColor

internal val MarkdownTheme.taskUncheckedBorderColor
    get() = taskList.uncheckedBorderColor

internal val MarkdownTheme.taskCornerRadius
    get() = taskList.cornerRadius

internal val MarkdownTheme.taskCheckmarkText
    get() = taskList.checkmarkText

internal val MarkdownTheme.taskCheckmarkTextStyle
    get() = taskList.checkmarkTextStyle

internal val MarkdownTheme.mathFontSize
    get() = math.fontSize.value

internal val MarkdownTheme.mathBlockBackground
    get() = math.background

internal val MarkdownTheme.mathColor
    get() = math.color

internal val MarkdownTheme.admonitionStyles
    get() = admonition.styles

internal val MarkdownTheme.admonitionFallbackStyle
    get() = admonition.fallback

internal val MarkdownTheme.admonitionPadding
    get() = admonition.padding

internal val MarkdownTheme.admonitionBorderWidth
    get() = admonition.borderWidth

internal val MarkdownTheme.admonitionTitleTextStyle
    get() = admonition.titleTextStyle

internal val MarkdownTheme.admonitionCornerRadius
    get() = admonition.cornerRadius

internal val MarkdownTheme.admonitionTitleContentSpacing
    get() = admonition.titleContentSpacing

internal val MarkdownTheme.admonitionIconSpacing
    get() = admonition.iconSpacing

internal val MarkdownTheme.admonitionContentTextStyle
    get() = admonition.contentTextStyle

internal val MarkdownTheme.footnoteStyle
    get() = footnote.textStyle

internal val MarkdownTheme.footnoteDefinitionLabelStyle
    get() = footnote.definitionLabelTextStyle

internal val MarkdownTheme.footnoteDefinitionPadding
    get() = footnote.definitionPadding

internal val MarkdownTheme.footnoteDefinitionIndent
    get() = footnote.definitionIndent

internal val MarkdownTheme.htmlBlockStyle
    get() = htmlBlock.textStyle

internal val MarkdownTheme.htmlBlockBackground
    get() = htmlBlock.background

internal val MarkdownTheme.htmlBlockCornerRadius
    get() = htmlBlock.cornerRadius

internal val MarkdownTheme.htmlBlockPadding
    get() = htmlBlock.padding

internal val MarkdownTheme.definitionListTermTextStyle
    get() = definitionList.termTextStyle

internal val MarkdownTheme.definitionListDescriptionIndent
    get() = definitionList.descriptionIndent

internal val MarkdownTheme.definitionListItemSpacing
    get() = definitionList.itemSpacing

internal val MarkdownTheme.diagramFallbackBackground
    get() = diagram.fallbackBackground

internal val MarkdownTheme.diagramFallbackTitleTextStyle
    get() = diagram.fallbackTitleTextStyle

internal val MarkdownTheme.diagramFallbackTitleBottomPadding
    get() = diagram.fallbackTitleBottomPadding

internal val MarkdownTheme.diagramFallbackIconEndPadding
    get() = diagram.fallbackIconEndPadding

internal val MarkdownTheme.diagramFallbackSpacerHeight
    get() = diagram.fallbackSpacerHeight

internal val MarkdownTheme.diagramNodeLabelTextStyle
    get() = diagram.nodeLabelTextStyle

internal val MarkdownTheme.diagramEdgeLabelTextStyle
    get() = diagram.edgeLabelTextStyle

internal val MarkdownTheme.diagramActorLabelTextStyle
    get() = diagram.actorLabelTextStyle

internal val MarkdownTheme.diagramCanvasInset
    get() = diagram.canvasInset

internal val MarkdownTheme.diagramCanvasPadding
    get() = diagram.canvasPadding

internal val MarkdownTheme.figureBackground
    get() = figure.container.background

internal val MarkdownTheme.figureBorderColor
    get() = figure.container.borderColor

internal val MarkdownTheme.figureBorderWidth
    get() = figure.container.borderWidth

internal val MarkdownTheme.figureCornerRadius
    get() = figure.container.cornerRadius

internal val MarkdownTheme.figurePadding
    get() = figure.container.padding

internal val MarkdownTheme.figureContentAlignment
    get() = figure.contentAlignment
