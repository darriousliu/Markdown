package com.hrm.markdown.ui.inline

import com.hrm.markdown.ui.MarkdownTheme

internal val MarkdownTheme.bodyStyle
    get() = paragraph.textStyle

internal val MarkdownTheme.inlineCodeStyle
    get() = inlineCode.textStyle

internal val MarkdownTheme.inlineCodeBackground
    get() = inlineCode.background

internal val MarkdownTheme.inlineCodeCornerRadius
    get() = inlineCode.cornerRadius

internal val MarkdownTheme.inlineCodePadding
    get() = inlineCode.padding

internal val MarkdownTheme.inlineCodeBorderColor
    get() = inlineCode.borderColor

internal val MarkdownTheme.inlineCodeBorderWidth
    get() = inlineCode.borderWidth

internal val MarkdownTheme.emphasisStyle
    get() = emphasis.textStyle

internal val MarkdownTheme.strongEmphasisStyle
    get() = strongEmphasis.textStyle

internal val MarkdownTheme.strikethroughStyle
    get() = strikethrough.textStyle

internal val MarkdownTheme.linkStyle
    get() = link.textStyle

internal val MarkdownTheme.linkColor
    get() = link.textStyle.color

internal val MarkdownTheme.inlineHtmlStyle
    get() = inlineHtml.textStyle

internal val MarkdownTheme.footnoteStyle
    get() = footnote.textStyle

internal val MarkdownTheme.mathFontSize
    get() = math.fontSize.value

internal val MarkdownTheme.mathColor
    get() = math.color

internal val MarkdownTheme.highlightColor
    get() = highlight.textStyle.background

internal val MarkdownTheme.superscriptStyle
    get() = superscript.textStyle

internal val MarkdownTheme.subscriptStyle
    get() = subscript.textStyle

internal val MarkdownTheme.insertedTextStyle
    get() = insertedText.textStyle

internal val MarkdownTheme.abbreviationStyle
    get() = abbreviation.textStyle

internal val MarkdownTheme.kbdStyle
    get() = kbd.textStyle

internal val MarkdownTheme.kbdBackground
    get() = kbd.background

internal val MarkdownTheme.kbdCornerRadius
    get() = kbd.cornerRadius

internal val MarkdownTheme.kbdPadding
    get() = kbd.padding

internal val MarkdownTheme.kbdBorderColor
    get() = kbd.borderColor

internal val MarkdownTheme.kbdBorderWidth
    get() = kbd.borderWidth

internal val MarkdownTheme.spoilerColor
    get() = spoiler.background
