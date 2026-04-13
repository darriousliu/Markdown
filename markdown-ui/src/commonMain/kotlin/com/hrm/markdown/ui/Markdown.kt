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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hrm.markdown.parser.ast.BlankLine
import com.hrm.markdown.parser.ast.Document
import com.hrm.markdown.ui.block.BlockRenderer
import com.hrm.markdown.ui.extension.MarkdownExtensionProvider
import com.hrm.markdown.ui.state.IncrementalBlockState
import com.hrm.markdown.ui.state.rememberMarkdownDocument
import com.hrm.markdown.ui.theme.MarkdownElementModifiers
import com.hrm.markdown.ui.theme.MarkdownTheme

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
    // ── 增量块状态 ──────────────────────────────────────────────────────────
    // IncrementalBlockState 持有两个 Snapshot 集合：
    //   nodeMap:  stableKey → Node（快照映射，每个 BlockCell 读取自己的条目）
    //   keyOrder: stableKey 有序列表（快照列表，BlockColumn 读取它）
    //
    // SideEffect 在每次 MarkdownContent 重组（= document 有新版本）后运行，
    // 对新旧节点列表做 O(N) 引用比较，只把真正变化的条目写入快照状态：
    //   - parser 复用的节点（同一引用）→ 不写入 → 对应 BlockCell 不重组
    //   - parser 产生新对象（内容变化）→ 写入  → 仅该 BlockCell 重组
    //   - 新增节点              → 插入  → BlockColumn 重组以追加新块
    val blockState = remember { IncrementalBlockState() }
    SideEffect {
        blockState.update(document.children.filter { it !is BlankLine })
    }

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
                blockState = blockState,
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

/**
 * 顶层块列表容器。
 *
 * 接受 [IncrementalBlockState] 而非 [Document]，使 Compose 能够在
 * [MarkdownContent] 因 document 参数变化而重组时跳过本函数：
 * [IncrementalBlockState] 是 [@Stable] 且持久存在于 remember 中，
 * 只有当 [IncrementalBlockState.keyOrder]（快照列表）真正发生增删时
 * 本函数才被触发重组。
 */
@Composable
private fun BlockColumn(
    blockState: IncrementalBlockState,
    modifier: Modifier,
    blockSpacing: androidx.compose.ui.unit.Dp,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(blockSpacing),
    ) {
        for (sk in blockState.keyOrder) {
            key(sk) {
                BlockCell(stableKey = sk, blockState = blockState)
            }
        }
    }
}

/**
 * 单块渲染单元——增量重组的最小粒度。
 *
 * ## 为什么这样设计？
 *
 * Compose 的跳过（skip）机制要求所有参数均为稳定类型且值不变。
 * 直接把 [com.hrm.markdown.parser.ast.Node] 作为参数行不通：
 *   - [Node] 位于无 Compose 依赖的 parser 模块，无法标注 [@Stable]；
 *   - 即便用 [@Stable] 包装，Compose 编译器对 `for-loop` 捕获变量
 *     仍会保守地设置 `$changed` 位，实际并不跳过。
 *
 * 正确做法：让 [BlockCell] 只接受纯稳定参数（[Int] + [@Stable] 引用），
 * 在函数体内部通过快照状态读取节点。读取 `blockState.nodeMap[stableKey]`
 * 会向 Compose 注册该条目的观察；当该条目变化（parser 产生新节点）时，
 * 仅此 [BlockCell] 被独立调度重组，其他块保持静止。
 *
 * 当 [BlockColumn] 因 [IncrementalBlockState.keyOrder] 增删而重组时，
 * 对已有的 [BlockCell] 调用会命中跳过检查：
 *   - `stableKey: Int`    → 原始类型，值相等即跳过
 *   - `blockState: @Stable` → 同一 `remember` 实例，引用不变即跳过
 */
@Composable
private fun BlockCell(stableKey: Int, blockState: IncrementalBlockState) {
    val node = blockState.nodeMap[stableKey] ?: return
    BlockRenderer(node)
}
