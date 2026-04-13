package com.hrm.markdown.ui.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import com.hrm.markdown.parser.ast.Node

/**
 * 渲染层增量块状态——把"哪些块需要重组"的决策权从 Compose 框架交还给应用层。
 *
 * ## 背景
 *
 * 流式场景下，每次 `append()` 都会产生新的 [com.hrm.markdown.parser.ast.Document]
 * 对象（[Document] 是不稳定类型），导致 `MarkdownContent` 每帧都重组，进而让
 * `BlockColumn` 对全部 N 个块执行重组检查——即使 parser 已经复用了绝大多数旧节点。
 *
 * ## 核心思路
 *
 * 把顶层块的状态拆分为两个细粒度 Snapshot 集合，供 `BlockCell` composable 独立观察：
 *
 * - [nodeMap]：`stableKey(起始行号) → Node` 的快照映射。
 *   `BlockCell` 只读取 `nodeMap[自己的key]`，该条目不变则 `BlockCell` 不重组。
 *
 * - [keyOrder]：stableKey 的有序列表。
 *   `BlockColumn` 读取它；只有列表发生增删时 `BlockColumn` 才重组。
 *
 * ## 工作流程
 *
 * ```
 * document 变化（每次 append）
 *   → MarkdownContent 重组
 *       → SideEffect 注册
 *       → BlockColumn(@Stable blockState) → 参数稳定，Compose 跳过！
 *   → SideEffect 执行：blockState.update(新节点列表)
 *       → 对新旧节点逐一比较对象引用（parser 复用的节点引用不变）
 *       → 仅写入真正变化的条目
 *   → Compose 快照通知：
 *       → nodeMap[tailKey] 变化 → 仅 BlockCell(tailKey) 重组
 *       → keyOrder 新增     → BlockColumn 重组，已有 BlockCell 命中跳过检查
 * ```
 *
 * 净效果：每次 `append()` 仅触发 O(变化块数) 次重组，通常为 1~2 次。
 */
@Stable
class IncrementalBlockState {

    /** stableKey(起始行号) → 当前 Node 的快照映射。 */
    val nodeMap = mutableStateMapOf<Int, Node>()

    /** 顶层块 stableKey 的有序快照列表。 */
    val keyOrder = mutableStateListOf<Int>()

    /**
     * 以新的块节点列表对当前状态进行增量对账。
     *
     * **必须在 Compose composition 之外调用**（例如 `SideEffect`、`LaunchedEffect`、协程），
     * 不得在 composable 函数体内直接调用（Compose 不允许在组合期间写入快照状态）。
     *
     * ### 复杂度
     * - 平均 O(N)，N 为顶层块数量。
     * - 流式追加场景：前缀节点均命中 `===` 快捷路径（无快照写入），
     *   只有末尾 1~2 个块触发快照写入和重组。
     *
     * @param incoming 最新的顶层块节点列表（已过滤 BlankLine）
     */
    fun update(incoming: List<Node>) {
        val incomingKeySet = incoming.mapTo(HashSet()) { it.stableKey }

        // ── 1. 移除不再出现的块 ─────────────────────────────────────────────
        val toRemove = nodeMap.keys.filter { it !in incomingKeySet }
        if (toRemove.isNotEmpty()) {
            val removeSet = toRemove.toHashSet()
            toRemove.forEach { nodeMap.remove(it) }
            keyOrder.removeAll { it in removeSet }
        }

        // ── 2. 更新内容已变的块 / 注册新块 ──────────────────────────────────
        // 关键：=== 比较对象引用。
        // parser 增量复用的节点引用不变 → 不写入快照 → 对应 BlockCell 不重组。
        // parser 产生新对象（内容变化）  → 写入快照   → 仅该 BlockCell 重组。
        for (node in incoming) {
            if (nodeMap[node.stableKey] !== node) {
                nodeMap[node.stableKey] = node
            }
        }

        // ── 3. 同步键顺序（流式场景新键始终在末尾，O(1) 均摊） ────────────
        if (keyOrder.size < incoming.size) {
            val existing = keyOrder.toHashSet()
            for (node in incoming) {
                if (existing.add(node.stableKey)) {
                    keyOrder.add(node.stableKey)
                }
            }
        }
        // 注：此处不处理块的重新排序（非流式编辑场景），
        // 因为 parser 的增量编辑模式不会改变前缀块的相对顺序。
    }
}
