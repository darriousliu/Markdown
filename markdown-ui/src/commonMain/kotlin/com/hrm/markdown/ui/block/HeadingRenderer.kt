package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.buildAnnotatedString
import com.hrm.markdown.parser.ast.Heading
import com.hrm.markdown.parser.ast.Node
import com.hrm.markdown.parser.ast.SetextHeading
import com.hrm.markdown.ui.LocalMarkdownDocument
import com.hrm.markdown.ui.LocalMarkdownHeadingNumbering
import com.hrm.markdown.ui.LocalMarkdownTheme
import com.hrm.markdown.ui.inline.rememberInlineContent

@Composable
internal fun HeadingRenderer(node: Heading, modifier: Modifier = Modifier) {
    HeadingContent(
        level = node.level,
        inlineParent = node,
        computeNumbering = { children ->
            if (LocalMarkdownHeadingNumbering.current) computeHeadingNumber(children, node) else null
        },
        underline = true,
        modifier = modifier,
    )
}

@Composable
internal fun SetextHeadingRenderer(node: SetextHeading, modifier: Modifier = Modifier) {
    HeadingContent(
        level = node.level,
        inlineParent = node,
        computeNumbering = { children ->
            if (LocalMarkdownHeadingNumbering.current) computeSetextHeadingNumber(children, node) else null
        },
        underline = true,
        modifier = modifier,
    )
}

@Composable
private fun HeadingContent(
    level: Int,
    inlineParent: com.hrm.markdown.parser.ast.ContainerNode,
    computeNumbering: @Composable (List<Node>) -> String?,
    underline: Boolean,
    modifier: Modifier,
) {
    val theme = LocalMarkdownTheme.current
    val headingStyles = theme.heading.textStyles
    val lvlIndex = (level - 1).coerceIn(0, headingStyles.lastIndex)
    val textStyle = headingStyles[lvlIndex]

    val (annotated, inlineContents) = rememberInlineContent(inlineParent)

    val document = LocalMarkdownDocument.current
    val numbering = computeNumbering(document.children)

    val display = if (numbering != null) {
        remember(numbering, annotated) {
            buildAnnotatedString {
                append("$numbering ")
                append(annotated)
            }
        }
    } else annotated

    val shouldUnderline = underline &&
        level <= theme.heading.underlineMaxLevel &&
        theme.heading.underlineThickness.value > 0f

    val strokeColor = theme.heading.underlineColor
    val strokeThicknessDp = theme.heading.underlineThickness
    val underlinePadding = theme.heading.underlinePadding

    val containerModifier = if (shouldUnderline) {
        modifier.drawBehind {
            val thicknessPx = strokeThicknessDp.toPx()
            val y = size.height
            drawLine(
                color = strokeColor,
                start = Offset(0f, y - thicknessPx / 2),
                end = Offset(size.width, y - thicknessPx / 2),
                strokeWidth = thicknessPx,
            )
        }
    } else modifier

    Column(modifier = containerModifier) {
        BasicText(text = display, style = textStyle, inlineContent = inlineContents)
        if (shouldUnderline) {
            Column(modifier = Modifier.padding(top = underlinePadding)) {}
        }
    }
}

private fun computeHeadingNumber(children: List<Node>, target: Heading): String? =
    computeNumber(children) { it === target || (it is Heading && it === target) }

private fun computeSetextHeadingNumber(children: List<Node>, target: SetextHeading): String? =
    computeNumber(children) { it === target || (it is SetextHeading && it === target) }

private fun computeNumber(
    children: List<Node>,
    isTarget: (Node) -> Boolean,
): String? {
    val counters = IntArray(6)
    for (child in children) {
        val level = when (child) {
            is Heading -> child.level
            is SetextHeading -> child.level
            else -> continue
        }
        val idx = (level - 1).coerceIn(0, 5)
        counters[idx]++
        for (i in idx + 1..5) counters[i] = 0
        if (isTarget(child)) {
            return (0..idx).joinToString(".") { counters[it].coerceAtLeast(1).toString() }
        }
    }
    return null
}
