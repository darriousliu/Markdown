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

internal val MarkdownTheme.blockQuotePadding
    get() = blockQuote.contentPadding

internal val MarkdownTheme.blockQuoteTextColor
    get() = blockQuote.textColor

internal val MarkdownTheme.blockQuoteBackground
    get() = blockQuote.backgroundColor

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

internal val MarkdownTheme.listIndent
    get() = list.indent

internal val MarkdownTheme.listBulletColor
    get() = list.bulletColor

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

internal val MarkdownTheme.linkColor
    get() = link.textStyle.color

internal val MarkdownTheme.taskCheckedColor
    get() = taskList.checkedColor

internal val MarkdownTheme.taskUncheckedColor
    get() = taskList.uncheckedColor

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
