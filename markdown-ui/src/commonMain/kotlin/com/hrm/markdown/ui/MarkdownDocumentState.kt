package com.hrm.markdown.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hrm.markdown.parser.MarkdownParser
import com.hrm.markdown.parser.ast.Document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Parser-only API：仅解析 Markdown，返回 AST 文档。
 */
@Composable
fun rememberMarkdownDocument(
    markdown: String,
    isStreaming: Boolean,
    config: MarkdownUiConfig = MarkdownUiConfig.Default,
    retainStateOnChange: Boolean = false,
): Document? {
    val parser = remember(config) {
        MarkdownParser(
            flavour = config.flavour,
            customEmojiMap = config.customEmojiMap,
            enableAsciiEmoticons = config.enableAsciiEmoticons,
            enableLinting = config.enableLinting,
        )
    }

    var state by remember(parser) { mutableStateOf(StreamingDocumentState<Document>()) }

    LaunchedEffect(markdown, isStreaming, parser, retainStateOnChange) {
        var currentState = state
        if (
            !retainStateOnChange &&
            !isStreaming &&
            !currentState.wasStreaming &&
            markdown != currentState.lastNonStreamingMarkdown &&
            currentState.document != null
        ) {
            currentState = currentState.copy(document = null)
            state = currentState
        }

        state = updateStreamingDocumentState(
            markdown = markdown,
            isStreaming = isStreaming,
            state = currentState,
            beginStream = parser::beginStream,
            append = parser::append,
            endStream = parser::endStream,
            parse = { value -> withContext(Dispatchers.Default) { parser.parse(value) } },
        )
    }

    return state.document
}

/**
 * 可选容器：提供通用解析 + UI 注入，不包含任何内置 renderer。
 */
@Composable
fun MarkdownDocument(
    markdown: String,
    isStreaming: Boolean,
    config: MarkdownUiConfig = MarkdownUiConfig.Default,
    retainStateOnChange: Boolean = false,
    loadingContent: (@Composable () -> Unit)? = null,
    content: @Composable (Document) -> Unit,
) {
    val document = rememberMarkdownDocument(
        markdown = markdown,
        isStreaming = isStreaming,
        config = config,
        retainStateOnChange = retainStateOnChange,
    )

    if (document == null) {
        if (!isStreaming) loadingContent?.invoke()
        return
    }

    content(document)
}

internal data class StreamingDocumentState<T>(
    val lastParsedLength: Int = 0,
    val document: T? = null,
    val wasStreaming: Boolean = false,
    val lastNonStreamingMarkdown: String = "",
)

internal suspend fun <T> updateStreamingDocumentState(
    markdown: String,
    isStreaming: Boolean,
    state: StreamingDocumentState<T>,
    beginStream: () -> Unit,
    append: (String) -> T,
    endStream: () -> T,
    parse: suspend (String) -> T?,
): StreamingDocumentState<T> {
    var nextState = state

    if (isStreaming && !nextState.wasStreaming) {
        beginStream()
        nextState = nextState.copy(lastParsedLength = 0, document = null, wasStreaming = true)
    }

    if (isStreaming) {
        if (markdown.length > nextState.lastParsedLength) {
            val chunk = markdown.substring(nextState.lastParsedLength)
            if (chunk.isNotEmpty()) {
                nextState = nextState.copy(
                    document = append(chunk),
                    lastParsedLength = markdown.length,
                )
            }
        }
        return nextState.copy(wasStreaming = true)
    }

    if (nextState.wasStreaming) {
        if (markdown.length > nextState.lastParsedLength) {
            val chunk = markdown.substring(nextState.lastParsedLength)
            if (chunk.isNotEmpty()) {
                nextState = nextState.copy(
                    document = append(chunk),
                    lastParsedLength = markdown.length,
                )
            }
        }
        return nextState.copy(
            document = endStream(),
            lastParsedLength = markdown.length,
            wasStreaming = false,
            lastNonStreamingMarkdown = markdown,
        )
    }

    if (markdown == nextState.lastNonStreamingMarkdown && nextState.document != null) {
        return nextState.copy(wasStreaming = false)
    }

    return nextState.copy(
        document = parse(markdown),
        lastParsedLength = markdown.length,
        wasStreaming = false,
        lastNonStreamingMarkdown = markdown,
    )
}
