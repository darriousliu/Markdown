package com.hrm.markdown.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.BlankLine
import com.hrm.markdown.parser.ast.Document
import com.hrm.markdown.parser.ast.Node
import com.hrm.markdown.ui.block.BlockRenderer
import com.hrm.markdown.ui.extension.MarkdownExtensionProvider
import com.hrm.markdown.ui.state.rememberMarkdownDocument
import com.hrm.markdown.ui.theme.MarkdownElementModifiers
import com.hrm.markdown.ui.theme.MarkdownTheme

/**
 * 稳定节点包装器：让 Compose 能够对未变化的块跳过重组。
 *
 * 问题根源：[Node] 位于无 Compose 依赖的 parser 模块，无法标注 [@Stable]，
 * 导致 Compose 编译器保守地认为每次父级重组时都必须重组 [BlockRenderer]，
 * 即便 [Node] 引用完全没有变化。
 *
 * 解决方案：在 UI 层引入此 [@Stable] 包装器。
 * - [BlockColumn] 为每个 [Node] 维护一个 [NodeStableWrapper] 缓存（wrapperCache）。
 * - parser 复用的节点（同一对象引用）会命中缓存，拿到相同的 [NodeStableWrapper] 实例。
 * - [BlockNode] 接受 [NodeStableWrapper]：Compose 看到 [@Stable] + 相同引用 → 直接跳过，
 *   不调用 [BlockRenderer]，也不触发任何下游 rememberInlineContent / collectInlineSlots。
 * - 仅当 parser 产生新 [Node] 对象（内容确实改变）时，缓存 miss → 新 [NodeStableWrapper] →
 *   Compose 重组 [BlockNode] → 仅该块重新渲染。
 */
@Stable
internal class NodeStableWrapper(val node: Node)

/**
 * markdown-ui 顶层 Composable 入口。
 *
 * 负责：
 * 1. 将原始 markdown 文本通过 [rememberMarkdownDocument] 转为 [Document]（内部已串行化、
 *    处理流式 / 非流式 / 流式结束后分歧等所有 corner case）。
 * 2. 注入所有 CompositionLocal（theme、modifiers、extension provider、link handler、streaming 标记等）。
 * 3. 遍历 Document 顶层子节点，交由 [BlockRenderer] 分发到各 element renderer。
 *
 * 该 Composable 内部 **不会** 强制 `fillMaxSize` / `fillMaxWidth` / 背景色。
 * 所有布局决定（宽度、高度、背景、padding、滚动容器）由调用方通过 [modifier] 与
 * [elementModifiers] 控制。
 *
 * 流式场景：将 [isStreaming] 设为 `true`，追加 token 时直接改动 [markdown] 即可，
 * 内部会做增量 append；当 token 流结束后再设为 `false`，会自动完成流式终结。
 *
 * @param markdown 原始 Markdown 文本
 * @param isStreaming 是否处于流式输出阶段
 * @param theme 主题
 * @param elementModifiers 每个元素的外部 Modifier
 * @param extensionProvider Flavor 扩展的 UI 实现（数学公式、图表、图片等）
 * @param config 解析器配置
 * @param onLinkClick 链接点击回调
 * @param scrollState 垂直滚动状态；设置为 `null` 表示不让内部自行滚动
 * @param enableSelection 非流式时是否包裹 SelectionContainer 支持文本选择
 * @param loading 未拿到 Document 前的占位 UI，默认空 Box
 */
@Composable
fun Markdown(
    markdown: String,
    modifier: Modifier = Modifier,
    isStreaming: Boolean = false,
    theme: MarkdownTheme = MarkdownTheme.Light,
    elementModifiers: MarkdownElementModifiers = MarkdownElementModifiers.None,
    extensionProvider: MarkdownExtensionProvider = MarkdownExtensionProvider.None,
    config: MarkdownUiConfig = MarkdownUiConfig.Default,
    onLinkClick: ((String) -> Unit)? = null,
    scrollState: ScrollState? = rememberScrollState(),
    enableSelection: Boolean = true,
    loading: @Composable () -> Unit = { Box(Modifier.fillMaxSize()) },
) {
    val document = rememberMarkdownDocument(
        markdown = markdown,
        isStreaming = isStreaming,
        config = config,
    )

    if (document == null) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            loading()
        }
        return
    }

    MarkdownContent(
        document = document,
        modifier = modifier,
        theme = theme,
        elementModifiers = elementModifiers,
        extensionProvider = extensionProvider,
        onLinkClick = onLinkClick,
        scrollState = scrollState,
        isStreaming = isStreaming,
        enableSelection = enableSelection,
        enableHeadingNumbering = config.enableHeadingNumbering,
    )
}

/**
 * 当调用方已经自行持有 [Document]（例如需要复用解析结果 / 多处渲染）时的入口。
 *
 * 和 [Markdown] 的区别是它不负责解析，纯粹做 CompositionLocal 注入 + 块级遍历。
 */
@Composable
fun MarkdownContent(
    document: Document,
    modifier: Modifier = Modifier,
    theme: MarkdownTheme = MarkdownTheme.Light,
    elementModifiers: MarkdownElementModifiers = MarkdownElementModifiers.None,
    extensionProvider: MarkdownExtensionProvider = MarkdownExtensionProvider.None,
    onLinkClick: ((String) -> Unit)? = null,
    scrollState: ScrollState? = rememberScrollState(),
    isStreaming: Boolean = false,
    enableSelection: Boolean = true,
    enableHeadingNumbering: Boolean = false,
) {
    CompositionLocalProvider(
        LocalMarkdownTheme provides theme,
        LocalMarkdownModifiers provides elementModifiers,
        LocalMarkdownExtensionProvider provides extensionProvider,
        LocalMarkdownLinkHandler provides onLinkClick,
        LocalMarkdownDocument provides document,
        LocalMarkdownIsStreaming provides isStreaming,
        LocalMarkdownHeadingNumbering provides enableHeadingNumbering,
    ) {
        val content: @Composable () -> Unit = {
            BlockColumn(
                document = document,
                modifier = modifier
                    .let { if (scrollState != null) it.verticalScroll(scrollState) else it }
                    .then(elementModifiers.document),
                blockSpacing = theme.document.blockSpacing,
            )
        }

        if (!isStreaming && enableSelection) {
            SelectionContainer { content() }
        } else {
            content()
        }
    }
}

@Composable
private fun BlockColumn(
    document: Document,
    modifier: Modifier,
    blockSpacing: androidx.compose.ui.unit.Dp,
) {
    // wrapperCache: Node 引用 → NodeStableWrapper 实例的身份映射。
    // 同一 Node 对象始终返回同一 NodeStableWrapper，
    // 确保 Compose 在 BlockNode(@Stable 参数不变) 时能够跳过重组。
    val wrapperCache = remember { HashMap<Node, NodeStableWrapper>() }

    val stableNodes = remember(document) {
        val incoming = document.children.filter { it !is BlankLine }
        // 清理不再出现的旧节点，防止缓存无限增长
        val incomingSet = incoming.toHashSet()
        val stale = wrapperCache.keys.filter { it !in incomingSet }
        stale.forEach { wrapperCache.remove(it) }
        // 复用旧 wrapper（node 对象相同）或为新节点创建 wrapper
        incoming.map { node -> wrapperCache.getOrPut(node) { NodeStableWrapper(node) } }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(blockSpacing),
    ) {
        for (wrapper in stableNodes) {
            key(wrapper.node::class, wrapper.node.stableKey) {
                BlockNode(wrapper)
            }
        }
    }
}

/**
 * 单个块级节点的稳定渲染入口。
 *
 * 接受 [@Stable] 的 [NodeStableWrapper]，使 Compose 在包装器引用不变时
 * 能够完全跳过此 composable（包括其内部的所有 rememberInlineContent 调用），
 * 实现渲染层的增量重组：只有 parser 实际产生新节点的块才会重新渲染。
 */
@Composable
private fun BlockNode(wrapper: NodeStableWrapper) {
    BlockRenderer(wrapper.node)
}
