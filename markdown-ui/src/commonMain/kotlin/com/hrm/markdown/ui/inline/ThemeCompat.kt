package com.hrm.markdown.ui.inline

import com.hrm.markdown.ui.MarkdownTheme

internal val MarkdownTheme.bodyStyle
    get() = paragraph.textStyle

internal val MarkdownTheme.inlineCodeStyle
    get() = inlineCode.textStyle

internal val MarkdownTheme.strikethroughStyle
    get() = strikethrough.textStyle

internal val MarkdownTheme.linkColor
    get() = link.textStyle.color

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

internal val MarkdownTheme.spoilerColor
    get() = spoiler.background
