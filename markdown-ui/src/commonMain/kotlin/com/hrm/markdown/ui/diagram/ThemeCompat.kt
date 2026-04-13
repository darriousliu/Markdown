package com.hrm.markdown.ui.diagram

import com.hrm.markdown.ui.MarkdownTheme

internal val MarkdownTheme.bodyStyle
    get() = paragraph.textStyle

internal val MarkdownTheme.codeBlockStyle
    get() = codeBlock.textStyle

internal val MarkdownTheme.codeBlockCornerRadius
    get() = codeBlock.cornerRadius

internal val MarkdownTheme.codeBlockPadding
    get() = codeBlock.padding
