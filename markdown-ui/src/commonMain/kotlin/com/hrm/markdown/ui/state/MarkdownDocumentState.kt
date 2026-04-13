package com.hrm.markdown.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hrm.markdown.parser.MarkdownParser
import com.hrm.markdown.parser.ast.Document
import com.hrm.markdown.ui.MarkdownUiConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * 解析器与 UI 之间的桥梁：接收 markdown 文本 + 流式开关，
 * 负责调用 parser 生成最新的 [Document]，并正确处理：
 *
 * - 流式增量 append → 增量更新 Document
 * - 流式结束（isStreaming=false）
 *    - 如果当前 markdown == 已经累积的流式文本：调用 `endStream()` 做最终化
 *    - 如果 markdown 发生变化（比如用户取消了生成、加载了另一段文本）：
 *      必须走 `parse(markdown)` 完整解析，否则会固化在一个与新文本无关的旧 AST 上。
 * - 非流式的普通文本变更：异步 `parse()`
 * - 并发保护：MarkdownParser 内部持有可变状态，多次快速的 markdown 变化
 *   可能让 `LaunchedEffect` 被取消/重启时与前一个 parse 调用重叠。
 *   使用 [Mutex] 串行化所有 parser 调用，避免脏状态。
 */
@Composable
fun rememberMarkdownDocument(
    markdown: String,
    isStreaming: Boolean,
    config: MarkdownUiConfig = MarkdownUiConfig.Default,
): Document? {
    val session = remember(config) { ParserSession(config) }

    var document by remember(session) { mutableStateOf<Document?>(null) }

    LaunchedEffect(session, markdown, isStreaming) {
        document = session.update(markdown, isStreaming)
    }

    return document
}

/**
 * 将 parser 实例、流式状态、mutex 封装在一起的会话对象。
 *
 * 这是 `remember` 的结果，保持稳定：
 * - 同一个 [MarkdownUiConfig] 对应同一个 [ParserSession]，config 改变才会重建。
 * - 所有对 [MarkdownParser] 的调用都通过 [mutex] 串行化，避免并发污染内部状态。
 */
internal class ParserSession(private val config: MarkdownUiConfig) {

    private val parser = MarkdownParser(
        flavour = config.flavour,
        customEmojiMap = config.customEmojiMap,
        enableAsciiEmoticons = config.enableAsciiEmoticons,
        enableLinting = config.enableLinting,
    )
    private val mutex = Mutex()

    /** 上一次提交给 parser 的 markdown。 */
    private var lastMarkdown: String? = null

    /** 流式会话累积的文本。非流式时为 null。 */
    private var streamedBuffer: StringBuilder? = null

    suspend fun update(markdown: String, isStreaming: Boolean): Document =
        mutex.withLock { runStep(markdown, isStreaming) }

    private suspend fun runStep(markdown: String, isStreaming: Boolean): Document {
        return try {
            when {
                isStreaming -> streamingStep(markdown)
                streamedBuffer != null -> endStreamStep(markdown)
                else -> nonStreamingStep(markdown)
            }.also { lastMarkdown = markdown }
        } catch (ce: CancellationException) {
            throw ce
        }
    }

    private suspend fun streamingStep(markdown: String): Document {
        val buffer = streamedBuffer ?: StringBuilder().also {
            // 开启一个新的流式会话
            parser.beginStream()
            streamedBuffer = it
            lastMarkdown = null
        }

        return when {
            // 常见 case：新文本以缓冲区为前缀，只追加尾部 chunk
            markdown.length >= buffer.length &&
                startsWith(markdown, buffer) -> {
                if (markdown.length > buffer.length) {
                    val chunk = markdown.substring(buffer.length)
                    buffer.append(chunk)
                    parser.append(chunk)
                } else {
                    // 没有新字符，也返回当前 document
                    parser.document
                }
            }
            // markdown 在流式过程中被替换了（回退或改写）：
            // 重置流式会话，按最新完整文本重新开始。
            else -> {
                parser.beginStream()
                buffer.setLength(0)
                if (markdown.isNotEmpty()) {
                    buffer.append(markdown)
                    parser.append(markdown)
                } else {
                    parser.document
                }
            }
        }
    }

    private suspend fun endStreamStep(markdown: String): Document {
        val buffer = streamedBuffer!!
        streamedBuffer = null

        // 如果当前 markdown 仍然是流式累积的那段文本（或只是额外追加一段尾部），
        // 那就把尾部追上并 endStream。
        if (markdown.length >= buffer.length && startsWith(markdown, buffer)) {
            if (markdown.length > buffer.length) {
                val chunk = markdown.substring(buffer.length)
                parser.append(chunk)
            }
            return parser.endStream()
        }

        // markdown 在结束时与流式缓冲分歧：
        // 用户可能取消了生成并换了内容，或直接把文本清空/替换。
        // 丢弃流式中间状态，走完整 parse 保证 AST 是与新 markdown 对应的。
        parser.abort()
        return parseNow(markdown)
    }

    private suspend fun nonStreamingStep(markdown: String): Document {
        // 相同文本命中缓存：直接返回上一次 parser 生成的 Document
        if (markdown == lastMarkdown) {
            return parser.document
        }
        return parseNow(markdown)
    }

    private suspend fun parseNow(markdown: String): Document = withContext(Dispatchers.Default) {
        parser.parse(markdown)
    }

    private fun startsWith(source: String, prefix: CharSequence): Boolean {
        if (source.length < prefix.length) return false
        for (i in 0 until prefix.length) {
            if (source[i] != prefix[i]) return false
        }
        return true
    }
}
