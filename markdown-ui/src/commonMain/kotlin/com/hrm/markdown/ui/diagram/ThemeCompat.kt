package com.hrm.markdown.ui.diagram

import com.hrm.markdown.ui.theme.MarkdownTheme

internal val MarkdownTheme.bodyStyle
    get() = paragraph.textStyle

internal val MarkdownTheme.codeBlockStyle
    get() = codeBlock.textStyle

internal val MarkdownTheme.codeBlockCornerRadius
    get() = codeBlock.cornerRadius

internal val MarkdownTheme.codeBlockPadding
    get() = codeBlock.padding

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
