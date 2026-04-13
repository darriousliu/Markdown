package com.hrm.markdown.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.hrm.markdown.parser.ast.FencedCodeBlock
import com.hrm.markdown.parser.ast.IndentedCodeBlock
import com.hrm.markdown.ui.LocalMarkdownTheme

/**
 * 围栏代码块渲染器（``` 或 ~~~）。
 *
 * markdown-ui 不绑定任何语法高亮库，仅以等宽字体渲染原始文本。
 * 若需要语法高亮，使用方可在 [com.hrm.markdown.ui.extension.MarkdownExtensionProvider]
 * 的 `Diagram` / 自定义容器中结合外部库（如 codehigh）接管渲染。
 */
@Composable
internal fun FencedCodeBlockRenderer(node: FencedCodeBlock, modifier: Modifier = Modifier) {
    CodeBlockContent(
        text = node.literal,
        title = node.info.takeIf { it.isNotBlank() },
        modifier = modifier,
    )
}

/**
 * 缩进代码块渲染器（4 空格缩进）。
 */
@Composable
internal fun IndentedCodeBlockRenderer(node: IndentedCodeBlock, modifier: Modifier = Modifier) {
    CodeBlockContent(text = node.literal, title = null, modifier = modifier)
}

@Composable
private fun CodeBlockContent(text: String, title: String?, modifier: Modifier) {
    val theme = LocalMarkdownTheme.current
    val style = theme.codeBlock

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(style.cornerRadius))
            .background(style.background),
    ) {
        if (!title.isNullOrEmpty()) {
            BasicText(
                text = title,
                modifier = Modifier
                    .background(style.titleBackground)
                    .padding(horizontal = style.padding, vertical = style.padding / 2),
                style = style.titleTextStyle,
            )
        }
        BasicText(
            text = text.trimEnd('\n').ifEmpty { " " },
            modifier = Modifier.padding(style.padding),
            style = style.textStyle,
        )
    }
}
