package com.hrm.markdown.parser

import com.hrm.markdown.parser.ast.*
import com.hrm.markdown.parser.incremental.EditOperation
import com.hrm.markdown.parser.incremental.IncrementalEngine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * 增量解析引擎和编辑功能测试。
 */
class IncrementalEngineTest {

    private fun Document.block(index: Int): Node = children[index]

    // ────── 全量解析（通过引擎） ──────

    @Test
    fun should_parse_full_document_via_engine() {
        val engine = IncrementalEngine()
        val doc = engine.fullParse("# Hello\n\nWorld")
        assertEquals(2, doc.children.size)
        assertTrue(doc.children[0] is Heading)
        assertTrue(doc.children[1] is Paragraph)
    }

    // ────── 编辑 API 测试 ──────

    @Test
    fun should_insert_text_and_reparse() {
        val engine = IncrementalEngine()
        val doc = engine.fullParse("# Hello\n\nWorld")
        val heading = doc.block(0)
        val paragraph = doc.block(1)

        // 在 "World" 后插入更多文本
        val updatedDoc = engine.applyEdit(EditOperation.Insert(offset = 14, text = " of Markdown"))
        val para = updatedDoc.children.last()
        assertTrue(para is Paragraph)
        assertSame(heading, updatedDoc.block(0), "未受影响的标题节点应该被复用")
        assertNotSame(paragraph, updatedDoc.block(1), "被编辑命中的段落节点应该重建")
    }

    @Test
    fun should_delete_text_and_reparse() {
        val engine = IncrementalEngine()
        val doc = engine.fullParse("# Hello\n\nWorld\n\nExtra")
        val heading = doc.block(0)
        val extra = doc.block(2)
        val extraOldStartLine = extra.lineRange.startLine

        // 删除 "World\n\n" 部分
        val updatedDoc = engine.applyEdit(EditOperation.Delete(offset = 9, length = 7))
        assertEquals(2, updatedDoc.children.size)
        assertSame(heading, updatedDoc.block(0), "脏区之前的节点应该被复用")
        assertSame(extra, updatedDoc.block(1), "脏区之后且内容未变的节点应该被复用")
        assertEquals(extraOldStartLine - 2, extra.lineRange.startLine, "复用的后缀节点需要同步更新行号")
    }

    @Test
    fun should_replace_text_and_reparse() {
        val engine = IncrementalEngine()
        val doc = engine.fullParse("# Hello\n\nWorld")
        val oldHeading = doc.block(0)
        val paragraph = doc.block(1)

        // 将 "# Hello" 替换为 "## Goodbye"
        val updatedDoc = engine.applyEdit(EditOperation.Replace(offset = 0, length = 7, newText = "## Goodbye"))
        val heading = updatedDoc.children.first()
        assertTrue(heading is Heading)
        assertEquals(2, (heading as Heading).level)
        assertNotSame(oldHeading, updatedDoc.block(0), "受影响的标题节点应该被重建")
        assertSame(paragraph, updatedDoc.block(1), "未受影响的后续段落应该被复用")
    }

    @Test
    fun should_handle_edit_after_stream_parse() {
        val parser = MarkdownParser()
        // 先做全量解析
        parser.parse("# Title\n\nParagraph one")
        // 然后用编辑 API 添加内容
        val doc = parser.insert(offset = 21, text = "\n\nParagraph two")
        assertTrue(doc.children.size >= 2)
    }

    @Test
    fun should_handle_empty_after_delete() {
        val parser = MarkdownParser()
        parser.parse("Hello")
        val doc = parser.delete(offset = 0, length = 5)
        assertTrue(doc.children.isEmpty() || doc.children.all { it is BlankLine })
    }

    @Test
    fun should_apply_edit_operation_directly() {
        val parser = MarkdownParser()
        parser.parse("# Hello\n\nWorld")
        val doc = parser.applyEdit(EditOperation.Insert(14, "\n\n## New Section"))
        assertTrue(doc.children.any { it is Heading && it.level == 2 })
    }

    @Test
    fun should_only_rebuild_middle_block_when_editing_inside_it() {
        val engine = IncrementalEngine()
        val markdown = "# Title\n\nAlpha\n\nBeta\n\nGamma"
        val doc = engine.fullParse(markdown)
        val heading = doc.block(0)
        val alpha = doc.block(1)
        val beta = doc.block(2)
        val gamma = doc.block(3)

        val updatedDoc = engine.applyEdit(
            EditOperation.Insert(
                offset = markdown.indexOf("Beta") + 2,
                text = " changed"
            )
        )

        assertSame(heading, updatedDoc.block(0), "前缀未变节点应该保持同一对象")
        assertSame(alpha, updatedDoc.block(1), "编辑块之前的兄弟节点应该被复用")
        assertNotSame(beta, updatedDoc.block(2), "命中的块节点应该被重建")
        assertSame(gamma, updatedDoc.block(3), "编辑块之后且内容未变的节点应该被复用")
    }

    @Test
    fun should_reuse_suffix_node_when_inserting_new_block_before_it() {
        val engine = IncrementalEngine()
        val markdown = "# Title\n\nAlpha\n\nGamma"
        val doc = engine.fullParse(markdown)
        val heading = doc.block(0)
        val alpha = doc.block(1)
        val gamma = doc.block(2)
        val gammaOldStartLine = gamma.lineRange.startLine

        val updatedDoc = engine.applyEdit(
            EditOperation.Insert(
                offset = markdown.indexOf("Gamma"),
                text = "Beta\n\n"
            )
        )

        assertEquals(4, updatedDoc.children.size)
        assertSame(heading, updatedDoc.block(0), "插入点之前的节点应该被复用")
        assertSame(alpha, updatedDoc.block(1), "未受影响的前缀节点应该被复用")
        assertTrue(updatedDoc.block(2) is Paragraph, "新增块应该出现在插入位置")
        assertSame(gamma, updatedDoc.block(3), "插入后的未变后缀节点应该被复用")
        assertEquals(gammaOldStartLine + 2, gamma.lineRange.startLine, "复用的后缀节点行号应该向后平移")
    }

    @Test
    fun should_only_rebuild_blocks_touching_removed_separator() {
        val engine = IncrementalEngine()
        val markdown = "# Title\n\nAlpha\n\nBeta\n\nTail"
        val doc = engine.fullParse(markdown)
        val heading = doc.block(0)
        val alpha = doc.block(1)
        val beta = doc.block(2)
        val tail = doc.block(3)

        val separatorOffset = markdown.indexOf("\n\nBeta")
        val updatedDoc = engine.applyEdit(EditOperation.Delete(offset = separatorOffset, length = 2))

        assertEquals(3, updatedDoc.children.size)
        assertSame(heading, updatedDoc.block(0), "与变更无关的前缀节点应该被复用")
        assertNotSame(alpha, updatedDoc.block(1), "跨块边界的编辑应重建受影响的块")
        assertNotSame(beta, updatedDoc.block(1), "合并后的块不能复用旧的相邻节点")
        assertSame(tail, updatedDoc.block(2), "尾部未变节点应该继续复用")
    }

    // ────── IncrementalEngine 流式测试 ──────

    @Test
    fun should_stream_via_incremental_engine() {
        val engine = IncrementalEngine()
        engine.beginStream()
        engine.append("# Hello")
        engine.append(" World\n\n")
        engine.append("This is text")
        val doc = engine.endStream()
        assertTrue(doc.children.isNotEmpty())
        assertTrue(doc.children[0] is Heading)
    }

    @Test
    fun should_handle_abort() {
        val engine = IncrementalEngine()
        engine.beginStream()
        engine.append("# Hello")
        engine.append("\n\nPartial **bold")
        val doc = engine.abort()
        // abort 后应该有有效文档
        assertTrue(doc.children.isNotEmpty())
    }

    // ────── 编辑场景：多次编辑 ──────

    @Test
    fun should_handle_multiple_sequential_edits() {
        val parser = MarkdownParser()
        parser.parse("# Title\n\nLine one")

        // 第一次编辑：在末尾追加
        val len1 = parser.currentText().length
        parser.insert(offset = len1, text = "\n\nLine two")

        // 第二次编辑：再追加
        val len2 = parser.currentText().length
        val doc = parser.insert(offset = len2, text = "\n\nLine three")
        assertTrue(doc.children.size >= 3)
    }

    @Test
    fun should_handle_insert_in_middle() {
        val parser = MarkdownParser()
        parser.parse("# Hello\n\nEnd")

        // 在段落之间插入新块
        val doc = parser.insert(offset = 9, text = "Middle\n\n")
        assertTrue(doc.children.size >= 2)
    }

    // ────── 编辑 + 块级结构 ──────

    @Test
    fun should_edit_inside_code_block() {
        val parser = MarkdownParser()
        parser.parse("```\ncode\n```\n\nText")
        // 在代码块内容中插入
        val doc = parser.insert(offset = 4, text = "more ")
        val codeBlock = doc.children.filterIsInstance<FencedCodeBlock>().firstOrNull()
        assertTrue(codeBlock != null)
        assertTrue(codeBlock.literal.contains("more "))
    }

    @Test
    fun should_edit_heading_level() {
        val parser = MarkdownParser()
        parser.parse("# Title\n\nText")
        // 将 # 替换为 ###
        val doc = parser.replace(offset = 0, length = 1, newText = "###")
        val heading = doc.children.first()
        assertTrue(heading is Heading)
        assertEquals(3, (heading as Heading).level)
    }
}
